package dataflow;

import ast.*;

import java.util.HashSet;
import java.util.Set;

public class Instruction {
    private final Statement statement;
    private final InstructionType type;
    private Instruction next;
    private final Set<Instruction> prev;
    private Instruction target;
    private final Set<Instruction> targeting;
    private int number;

    public static final Instruction END =
            new Instruction(InstructionType.END, -1);

    public static Instruction createStart(Instruction to) {
        var start = new Instruction(InstructionType.START, 0);
        start.next = to;
        to.prev.add(start);
        return start;
    }

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
        } else if (statement instanceof Return) {
            type = InstructionType.RETURN;
        }

        return new Instruction(statement, type, next);
    }

    public Instruction(Statement statement, InstructionType type,
                       Instruction next, Instruction target) {
        this.statement = statement;
        this.type = type;
        this.next = next;
        this.target = target;
        this.prev = new HashSet<>();
        this.targeting = new HashSet<>();

        if (next != null) next.prev.add(this);
        if (target != null) target.targeting.add(this);
    }

    public Instruction(Statement statement, InstructionType type,
                       Instruction next) {
        this(statement, type, next, null);
    }

    public Instruction(InstructionType type, int number) {
        this(null, type, null, null);
        this.number = number;
    }

    public Instruction getNext() {
        return next;
    }

    public Instruction getTarget() {
        return target;
    }

    public Statement getStatement() {
        return statement;
    }

    public InstructionType getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    public void setNext(Instruction next) {
        if (this.next != null) this.next.prev.remove(this);
        this.next = next;
        next.prev.add(this);
    }

    public void setTarget(Instruction target) {
        if (this.target != null) this.target.targeting.remove(this);
        this.target = target;
        target.targeting.add(this);
    }

    public void propagateReferences() {
        var itr = prev.iterator();
        while (itr.hasNext()) {
            var p = itr.next();
            p.next = next;
            next.prev.add(p);
            itr.remove();
        }

        itr = targeting.iterator();
        while (itr.hasNext()) {
            var t = itr.next();
            t.target = next;
            next.targeting.add(t);
            itr.remove();
        }

        // assert there are no instructions left pointing to this one
        assert(prev.isEmpty());
        assert(targeting.isEmpty());
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isJump() {
        return type == InstructionType.JUMP;
    }
}
