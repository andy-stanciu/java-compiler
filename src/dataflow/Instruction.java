package dataflow;

import ast.*;

public class Instruction {
    private final Statement statement;
    private final InstructionType type;
    private Instruction next;
    private Instruction target;
    private int number;

    public static Instruction createJump(Instruction to) {
        return new Instruction(null, InstructionType.JUMP, to);
    }

    public static Instruction createElse(Instruction elseBlock) {
        return new Instruction(null, InstructionType.ELSE, elseBlock);
    }

    public static Instruction fromStatement(Statement statement) {
        return fromStatement(statement, null);
    }

    public static Instruction fromStatement(Statement statement,
                                            Instruction next) {
        InstructionType type = InstructionType.BASIC;

        if (statement instanceof Block) {
            type = InstructionType.BLOCK;
        } else if (statement instanceof If) {
            type = InstructionType.IF;
        } else if (statement instanceof IfElse) {
            type = InstructionType.IF;
        } else if (statement instanceof While) {
            type = InstructionType.WHILE;
        } else if (statement instanceof For) {
            type = InstructionType.FOR;
        } else if (statement instanceof Switch) {
            type = InstructionType.SWITCH;
        }

        return new Instruction(statement, type, next);
    }

    public static final Instruction END =
            new Instruction(InstructionType.END, -1);

    public Instruction(Statement statement, InstructionType type,
                       Instruction next, Instruction target) {
        this.statement = statement;
        this.type = type;
        this.next = next;
        this.target = target;
    }

    public Instruction(Statement statement, InstructionType type,
                       Instruction next) {
        this(statement, type, next, null);
    }

    public Instruction(InstructionType type, int number) {
        this(null, type, null, null);
        this.number = number;
    }

    public Statement getStatement() {
        return statement;
    }

    public Instruction getTarget() {
        return target;
    }

    public Instruction getNext() {
        return next;
    }

    public InstructionType getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setNext(Instruction next) {
        this.next = next;
    }

    public void setTarget(Instruction target) {
        this.target = target;
    }

    public boolean isJump() {
        return type == InstructionType.JUMP;
    }
}
