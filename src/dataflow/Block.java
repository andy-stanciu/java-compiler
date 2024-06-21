package dataflow;

import java.util.*;
import java.util.function.Consumer;

public final class Block implements Iterable<Instruction> {
    private final BlockType type;
    private final List<Instruction> instructions;
    private final Set<Block> next;
    private int number;

    public static Block createStart(Instruction start) {
        var block = new Block(start, BlockType.START);
        start.setBlock(block);
        return block;
    }

    public static final Block END = new Block(BlockType.END);

    public static Block fromLeader(Instruction leader) {
        var block = new Block(leader, BlockType.BASIC);
        leader.setBlock(block);
        return block;
    }

    private Block(BlockType type) {
        this.instructions = new ArrayList<>();
        this.next = new HashSet<>();
        this.type = type;
    }

    private Block(Instruction leader, BlockType type) {
        this(type);
        this.instructions.add(leader);
    }

    public void addInstruction(Instruction instruction) {
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

    public BlockType getType() {
        return type;
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

    public Instruction getInstruction(int i) {
        return instructions.get(i);
    }
}
