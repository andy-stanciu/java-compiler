package dataflow;

import java.util.*;
import java.util.function.Consumer;

public final class Block implements Iterable<Instruction> {
    private final List<Instruction> instructions;
    private final Set<Block> next;
    private int number;

    public static final Block END = new Block();

    public static Block fromLeader(Instruction leader) {
        var block = new Block(leader);
        leader.setBlock(block);
        return block;
    }

    private Block(Instruction leader) {
        this();
        this.instructions.add(leader);
    }

    private Block() {
        this.instructions = new ArrayList<>();
        this.next = new HashSet<>();
    }

    public void addInstruction(Instruction instruction) {
        instruction.setbIndex(instructions.size());
        instructions.add(instruction);
        instruction.setBlock(this);
    }

    public Instruction getLeader() {
        if (instructions.isEmpty()) {
            throw new IllegalStateException();
        }
        return instructions.get(0);
    }

    public Set<Block> getNext() {
        return next;
    }

    public int getNumber() {
        return number;
    }

    public void addNext(Block next) {
        this.next.add(next);
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean hasNext() {
        return !next.isEmpty();
    }

    @Override
    public Iterator<Instruction> iterator() {
        return instructions.iterator();
    }

    @Override
    public void forEach(Consumer<? super Instruction> action) {
        for (var i : this) {
            action.accept(i);
        }
    }

    public int instructionCount() {
        return instructions.size();
    }
}
