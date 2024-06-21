package dataflow;

import ast.*;
import ast.visitor.dataflow.DataflowVisitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;
import semantics.info.MethodInfo;
import semantics.table.SymbolContext;
import semantics.type.TypeVoid;

import java.util.Deque;
import java.util.LinkedList;

public final class DataflowGraph {
    private final DataflowVisitor dataflowVisitor;
    private final Logger logger;
    private final MethodInfo method;
    private final StatementList statements;
    private Deque<Instruction> instructionGraph;
    private Deque<Block> blockGraph;

    /**
     * Initializes a new {@link DataflowGraph}.
     * @param symbolContext The current symbol context.
     * @param method The method containing the statements.
     * @param statements The statements to track.
     * @return A new {@link DataflowGraph}.
     */
    public static DataflowGraph create(SymbolContext symbolContext,
                                       MethodInfo method,
                                       StatementList statements) {
        return new DataflowGraph(symbolContext, method, statements);
    }

    private DataflowGraph(SymbolContext symbolContext,
                          MethodInfo method,
                          StatementList statements) {
        this.dataflowVisitor = new DataflowVisitor(symbolContext);
        this.logger = Logger.getInstance();
        this.method = method;
        this.statements = statements;
    }

    /**
     * Constructs this dataflow graph.
     * @return This {@link DataflowGraph}.
     */
    public DataflowGraph build() {
        constructInstructionGraph(statements);
        reduceInstructionGraph();
        assignInstructionNumbers();
        constructBlockGraph();
        assignBlockNumbers();
        return this;
    }

    /**
     * Validates return statements in this dataflow graph.
     * @return This {@link DataflowGraph}.
     */
    public DataflowGraph validateReturnStatements() {
        if (instructionGraph == null) {
            throw new IllegalStateException();
        }
        validateReturn();
        return this;
    }

    /**
     * Prints the instructions in this dataflow graph.
     */
    public void printInstructions() {
        if (instructionGraph == null) {
            throw new IllegalStateException();
        }
        printInstructionGraph();
    }

    /**
     * Prints the blocks in this dataflow graph.
     */
    public void printBlocks() {
        if (blockGraph == null) {
            throw new IllegalStateException();
        }
        printBlockGraph();
    }

    private void constructBlockGraph() {
        var istart = instructionGraph.peekFirst();
        assert(istart != null);

        blockGraph = new LinkedList<>();
        var start = Block.createStart(istart);
        blockGraph.offerLast(start);

        var ifirst = istart.getNext();
        if (ifirst.getType() != InstructionType.END) {
            var first = Block.fromLeader(ifirst);
            start.addNext(first);
            constructBlockGraphRec(first);
        } else {
            start.addNext(Block.END);
        }
    }

    private void constructBlockGraphRec(Block block) {
        blockGraph.offerLast(block);

        var i = block.getLeader();
        if (i.hasNext()) {
            var n = i.getNext();
            while (n != null && !n.isLeader()) {
                block.addInstruction(n);
                if (n.hasTarget()) {
                    buildBlock(block, n.getTarget());
                }
                n = n.getNext();
            }
            buildBlock(block, n);
        }

        if (i.hasTarget()) {
            buildBlock(block, i.getTarget());
        }
    }

    private void buildBlock(Block prev, Instruction i) {
        if (i == null) {
            return;
        }

        if (i.getType() == InstructionType.END) {
            prev.addNext(Block.END);
            return;
        }

        // invariant - i is a leader at this point
        boolean remaining = !i.hasBlock();
        var block = remaining ? Block.fromLeader(i) : i.getBlock();
        prev.addNext(block);

        if (remaining) {
            constructBlockGraphRec(block);
        }
    }

    private void printBlockGraph() {
        blockGraph.forEach(b -> {
            System.out.print(b.getType() == BlockType.START ? "<start>" :
                    ("Block " + b.getNumber()));
            if (b.hasNext()) {
                System.out.print(" -> ");
                int i = 0;
                for (var n : b.getNext()) {
                    if (i++ != 0) System.out.print(", ");
                    System.out.print(n.getType() == BlockType.END ? "<end>" :
                            n.getNumber());
                }
            }
            System.out.println();
            b.forEach(i -> {
                System.out.print("\t");
                printInstruction(i);
            });
        });
    }

    private void validateReturn() {
        boolean isVoid = method.returnType.equals(TypeVoid.getInstance());

        for (var b : blockGraph) {
            int c = 0;

            for (var i : b) {
                if (i.getType() == InstructionType.RETURN) {
                    // is this the last instruction in the block? if not,
                    // the rest of the instructions in the block are
                    // unreachable!
                    if (c < b.instructionCount() - 1) {
                        var next = b.getInstruction(c + 1);
                        // if the next instruction was already marked as
                        // unreachable, no need to re-report it
                        if (!next.unreachable) {
                            next.unreachable = true;
                            logger.setLineNumber(next.lineNumber);
                            logger.logError("Unreachable statement%n");
                        }
                        // we're done with this block now. we want to ignore
                        // any further issues that may come up, so we break
                        break;
                    }
                } else {
                    // if anything that is not a return statement is followed
                    // by the end, then we are missing a return statement
                    if (!isVoid && i.reachesEnd()) {
                        if (i.getNext() != null &&
                                i.getNext().getType() == InstructionType.END) {
                            logger.setLineNumber(i.getNext().lineNumber);
                        } else if (i.getTarget() != null &&
                                i.getTarget().getType() == InstructionType.END) {
                            logger.setLineNumber(i.getTarget().lineNumber);
                        }
                        logger.logError("Missing return statement%n");
                    }
                }
                c++;
            }
        }
    }

    private void reduceInstructionGraph() {
        var itr = instructionGraph.iterator();
        while (itr.hasNext()) {
            var i = itr.next();
            if (i.getType() != InstructionType.BLOCK &&
                    i.getType() != InstructionType.ELSE) {
                continue;
            }

            i.propagateReferences();
            itr.remove();
        }
    }

    private void constructInstructionGraph(StatementList statements) {
        var end = Instruction.createEnd(method.endLineNumber);
        var instructions = constructInstructionGraphRec(statements, end);

        var first = instructions.peekFirst();
        if (first != null) {  // start instruction
            instructions.offerFirst(Instruction.createStart(method.lineNumber, first));
        } else {
            instructions.offerFirst(Instruction.createStart(method.lineNumber, end));
        }

        instructionGraph = instructions;
    }

    private Deque<Instruction> constructInstructionGraphRec(StatementList statements,
                                                            Instruction end) {
        Deque<Instruction> remaining = new LinkedList<>();
        Deque<Instruction> done = new LinkedList<>();

        for (var statement : statements) {
            remaining.offerLast(Instruction.fromStatement(statement));
        }

        while (!remaining.isEmpty()) {
            var i = remaining.pollFirst();
            var nexti = remaining.isEmpty() ? end : remaining.peekFirst();

            done.offerLast(i);

            if (i.isJump()) {
                continue;
            }

            var s = i.getStatement();
            if (s instanceof ast.Block b) {
                var block = constructInstructionGraphRec(b.sl, end);
                if (!block.isEmpty()) {
                    i.setNext(block.peekFirst());
                    block.forEach(blocki -> {
                        if (blocki.getNext() != null &&
                                blocki.getNext().getType() == InstructionType.END &&
                                !blocki.isJump()) {
                            if (nexti.getType() == InstructionType.ELSE) {
                                assert(nexti.getNext() != null);
                                blocki.setNext(nexti.getNext().getNext());
                            } else {
                                blocki.setNext(nexti);
                            }
                        }
                        if (blocki.getTarget() != null &&
                                blocki.getTarget().getType() == InstructionType.END) {
                            if (nexti.getType() == InstructionType.ELSE) {
                                assert(nexti.getNext() != null);
                                blocki.setTarget(nexti.getNext().getNext());
                            } else {
                                blocki.setTarget(nexti);
                            }
                        }
                        done.offerLast(blocki);
                    });
                } else {
                    i.setNext(nexti);
                }
            } else if (s instanceof If if_) {
                var ifBranch = Instruction.fromStatement(if_.s, nexti);
                remaining.offerFirst(ifBranch);
                i.setNext(ifBranch);
                var temp = done.pollLast();
                var prev = done.peekLast();
                done.offerLast(temp);
                if (prev != null && prev.getType() == InstructionType.FOR) {
                    assert(nexti != null);
                } else {
                    i.setTarget(nexti);
                }
            } else if (s instanceof IfElse ifelse) {
                var elseBranch = Instruction.fromStatement(ifelse.s2, nexti);
                var ifBranch = Instruction.fromStatement(ifelse.s1, nexti);
                var else_ = Instruction.createElse(elseBranch);
                remaining.offerFirst(elseBranch);
                remaining.offerFirst(else_);
                remaining.offerFirst(ifBranch);
                i.setNext(ifBranch);
                i.setTarget(else_);
                ifBranch.setNext(nexti);
            } else if (s instanceof While w) {
                var jump = Instruction.createJump(i);
                var body = Instruction.fromStatement(w.s, jump);
                remaining.offerFirst(jump);
                remaining.offerFirst(body);
                i.setNext(body);
                i.setTarget(nexti);
            } else if (s instanceof For f) {
                var pos = new Location(0 ,0);
                var sl = new StatementList(pos);
                sl.add(f.s2);
                sl.add(f.s1);
                var if_ = new If(f.e, new ast.Block(sl, pos), pos);
                var loop = Instruction.fromStatement(if_);
                var jump = Instruction.createJump(loop);
                remaining.offerFirst(jump);
                remaining.offerFirst(loop);
                i.setNext(loop);
                loop.setTarget(nexti);
            } else if (s instanceof Switch sw) {
                // figure this out later lol
            } else {
                i.setNext(nexti);
            }
        }

        return done;
    }

    private void printInstructionGraph() {
        instructionGraph.forEach(this::printInstruction);
    }

    private void printInstruction(Instruction i) {
        System.out.print(i.getNumber() + ": ");
        switch (i.getType()) {
            case JUMP -> System.out.print("jump ");
            case ELSE -> System.out.print("else");
            case START -> System.out.print("<start>");
            default -> i.getStatement().accept(dataflowVisitor);
        }

        String next = i.getNext() == null ? "" :
                i.getNext().getType() == InstructionType.END ? "<end>" :
                        String.valueOf(i.getNext().getNumber());
        String target = i.getTarget() == null ? "" :
                i.getTarget().getType() == InstructionType.END ? "<end>" :
                        String.valueOf(i.getTarget().getNumber());
        System.out.println(target + (next.isEmpty() ? next : " -> " + next));
    }

    private void assignInstructionNumbers() {
        int n = 0;
        for (var i : instructionGraph) {
            i.setNumber(n++);
        }
    }

    private void assignBlockNumbers() {
        int n = 0;
        for (var b : blockGraph) {
            b.setNumber(n++);
        }
    }
}
