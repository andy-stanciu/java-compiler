package dataflow;

import ast.*;
import ast.visitor.dataflow.DataflowVisitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

import java.util.Deque;
import java.util.LinkedList;

public final class DataflowGraph {
    private final StatementList statements;
    private int instructionNumber;

    public static DataflowGraph create(StatementList statements) {
        return new DataflowGraph(statements);
    }

    private DataflowGraph(StatementList statements) {
        this.statements = statements;
    }

    public DataflowGraph compute() {
        var graph = constructInstructionGraph(statements);
        printInstructionGraph(graph);
        return this;
    }

    private void printInstructionGraph(Deque<Instruction> graph) {
        var v = new DataflowVisitor();

        graph.forEach(i -> {
            System.out.print(i.getNumber() + ": ");
            switch (i.getType()) {
                case JUMP -> System.out.print("jump");
                case ELSE -> System.out.print("else");
                default -> i.getStatement().accept(v);
            }

            String next = i.getNext() == Instruction.END ? "<end>" :
                    String.valueOf(i.getNext().getNumber());
            String target = i.getTarget() == Instruction.END ? "<end>" :
                    i.getTarget() == null ? "" : String.valueOf(i.getTarget().getNumber());
            System.out.println(target + " -> " + next);
        });
    }

    private Deque<Instruction> constructInstructionGraph(StatementList statements) {
        Deque<Instruction> remaining = new LinkedList<>();
        Deque<Instruction> done = new LinkedList<>();

        for (var statement : statements) {
            remaining.offer(Instruction.fromStatement(statement));
        }

        while (!remaining.isEmpty()) {
            var i = remaining.pollFirst();
            var nexti = remaining.isEmpty() ? Instruction.END : remaining.peekFirst();

            done.offerLast(i);
            i.setNumber(instructionNumber);
            instructionNumber++;

            if (i.isJump()) {
                continue;
            }

            var s = i.getStatement();
            if (s instanceof Block b) {
                var block = constructInstructionGraph(b.sl);
                if (!block.isEmpty()) {
                    i.setNext(block.peekFirst());
                    block.forEach(blocki -> {
                        if (blocki.getNext() == Instruction.END && !blocki.isJump()) {
                            if (nexti.getType() == InstructionType.ELSE) {
                                assert(nexti.getNext() != null);
                                blocki.setNext(nexti.getNext().getNext());
                            } else {
                                blocki.setNext(nexti);
                            }
                        }
                        if (blocki.getTarget() == Instruction.END) {
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
                var if_ = new If(f.e, new Block(sl, pos), pos);
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
}
