package dataflow;

import ast.*;
import ast.visitor.dataflow.DataflowVisitor;
import ast.visitor.dataflow.LiveVariableVisitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;
import semantics.info.ClassInfo;
import semantics.info.MethodInfo;
import semantics.table.SymbolContext;
import semantics.type.TypeVoid;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class DataflowGraph {
    private final DataflowVisitor dataflowVisitor;
    private final Logger logger;
    private final MethodInfo method;
    private final ClassInfo class_;
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
        this.class_ = symbolContext.getCurrentClass();
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
     * This includes checking for unreachable code and
     * ensuring that every branch returns at some point.
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
     * Validates variable declarations in this dataflow graph.
     * This includes checking for uninitialized variables.
     * @return This {@link DataflowGraph}.
     */
    public DataflowGraph validateVariableDeclarations() {
        if (instructionGraph == null) {
            throw new IllegalStateException();
        }

        validateDeclarations();
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

    private void validateDeclarations() {
        var liveVariableVisitor = new LiveVariableVisitor();

        // compute use and def sets for each block
        visitBlocks(b -> {
            // Def set of first block should include method parameters and instance variables
            if (b.isFirst()) {
                method.getArgumentNames().forEach(arg -> {
                    b.variables().defSet().add(new Symbol(arg, method.lineNumber));
                });
                class_.getInstanceVariables().forEach(v -> {
                    b.variables().defSet().add(new Symbol(v.name, v.lineNumber));
                });
            }
            b.forEach(i -> {
                var s = i.getStatement();
                if (s != null) {
                    s.accept(liveVariableVisitor);
                    b.variables().defSet().addAll(s.defined);

                    s.used.forEach(v -> {
                        if (!b.variables().defSet().contains(v)) {
                            b.variables().useSet().add(v);
                        }
                    });
                }
            });
        });

        // compute in and out sets for each block
        int previousSize = 0;
        while (true) {
            var size = new AtomicInteger();
            visitBlocks(b -> {
                // in[b] = use[b] union (out[b] - def[b])
                var diff = new HashSet<>(b.variables().outSet());
                diff.removeAll(b.variables().defSet());
                b.variables().inSet().addAll(b.variables().useSet());
                b.variables().inSet().addAll(diff);

                // out[b] = union of in[s] for all s in succ[b]
                b.getNext().forEach(s -> b.variables().outSet().addAll(s.variables().inSet()));

                // update with sizes of in & out sets
                size.getAndAdd(b.variables().inSet().size());
                size.getAndAdd(b.variables().outSet().size());
            });

            // if size is unchanged, we're done updating in & out sets
            if (previousSize == size.get()) {
                break;
            }

            // update previous size
            previousSize = size.get();
        }

        var start = blockGraph.pollFirst();
        var first = blockGraph.peekFirst();
        blockGraph.offerFirst(start);

        if (first == null) {  // if we don't have a first block, we're done with validation
            return;
        }

        // all live variable validation sets have been constructed.
        // any variables belonging to in[first block] that are not method parameters
        // or instance variables at this point must be uninitialized
        first.variables().inSet().forEach(v -> {
            logger.setLineNumber(v.lineNumber());
            logger.logError("Uninitialized variable \"%s\"%n", v.name());
        });
    }

    /**
     * Visits this dataflow graph's blocks in topological order.
     * @param action The action to apply to each block.
     */
    private void visitBlocks(Consumer<Block> action) {
        if (blockGraph.isEmpty()) {
            throw new IllegalStateException();
        }
        var b = blockGraph.peekFirst();
        Set<Block> visited = new HashSet<>();
        visited.add(b);
        visitBlocksRec(b, action, visited);
    }

    private void visitBlocksRec(Block b, Consumer<Block> action, Set<Block> visited) {
        b.getNext().forEach(s -> {
            if (!visited.contains(s) && s.getType() != BlockType.END) {
                action.accept(s);
            }
        });

        b.getNext().forEach(s -> {
            if (!visited.contains(s)) {
                visited.add(s);
                visitBlocksRec(s, action, visited);
            }
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
                    // unreachable (unless we have a jump)
                    if (c < b.instructionCount() - 1) {
                        var next = b.getInstruction(c + 1);
                        // if the next instruction is a jump, then we're good
                        if (next.isJump()) {
                            continue;
                        }
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
                // TODO: validate/fix this if it doesn't work
                var pos = new Location(0 ,0);
                for (int j = sw.cl.size() - 1; j >= 0; j--) {
                    var case_ = sw.cl.get(j);
                    var block = new ast.Block(case_.sl, pos);
                    Instruction c;
                    if (case_ instanceof CaseSimple cs) {
                        var if_ = new If(new Equal(sw.e, new IntegerLiteral(cs.n, pos), pos),
                                block, pos);
                        c = Instruction.fromStatement(if_);
                    } else {
                        // default case: no conditional
                        c = Instruction.fromStatement(block);
                    }

                    // if the case breaks and we're not the last case,
                    // we need to add unconditional jump to nexti after case
//                    if (case_.breaks && j != sw.cl.size() - 1) {
//                        var jump = Instruction.createJump(nexti);
//                        remaining.offerFirst(jump);
//                    }

                    remaining.offerFirst(c);
                    i.setNext(c);
                    c.setTarget(nexti);
                }
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
