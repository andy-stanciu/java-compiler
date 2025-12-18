package phase;

import ast.Program;
import dataflow.visitor.DataflowVisitor;
import codegen.platform.isa.ISA;
import java_cup.runtime.ComplexSymbolFactory;
import lombok.Builder;
import lombok.Data;
import scanner.*;
import commons.Logger;
import semantics.table.SymbolContext;

import java.io.File;

@Builder
@Data
public final class CompilerState {
    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_FAILURE = 1;

    private final int status;
    private final File sourceFile;
    private final ComplexSymbolFactory symbolFactory;
    private final scanner scanner;
    private final Program ast;
    private final Logger logger;
    private final SymbolContext symbolContext;
    private final ISA isa;
    private final DataflowVisitor dataflowVisitor;
}
