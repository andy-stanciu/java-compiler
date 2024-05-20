package ast.visitor.util;

import codegen.BooleanContext;

public final class FlowContext {
    private BooleanContext context;

    public static FlowContext create() {
        return new FlowContext();
    }

    private FlowContext() {}

    public void push(BooleanContext context) {
        this.context = context;
    }

    public BooleanContext pop() {
        var ret = context;
        context = null;
        return ret;
    }
}
