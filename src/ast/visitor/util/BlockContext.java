package ast.visitor.util;

public final class BlockContext {
    private BlockType context;

    public static BlockContext create() {
        return new BlockContext();
    }

    private BlockContext() {
        this.context = BlockType.BLOCK;
    }

    public void push(BlockType context) {
        this.context = context;
    }

    public BlockType peek() {
        return context;
    }

    public BlockType pop() {
        var ret = context;
        context = BlockType.BLOCK;
        return ret;
    }
}
