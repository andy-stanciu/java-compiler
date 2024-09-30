package codegen;

import codegen.platform.Label;

public record FlowContext(Label targetLabel, boolean jumpIf) {}
