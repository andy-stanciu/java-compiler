package phase;

import semantics.visitor.ClassVisitor;
import semantics.visitor.GlobalVisitor;
import semantics.visitor.LocalVisitor;
import commons.Logger;
import semantics.table.SymbolContext;

import static phase.CompilerState.EXIT_FAILURE;
import static phase.CompilerState.EXIT_SUCCESS;

public final class StaticAnalysisPhase implements CompilerPhase {
    private final boolean printSymbolTables;

    public StaticAnalysisPhase() {
        this(false);
    }

    public StaticAnalysisPhase(boolean printSymbolTables) {
        this.printSymbolTables = printSymbolTables;
    }

    @Override
    public CompilerState run(final CompilerState inputState) {
        assert inputState.getStatus() != EXIT_FAILURE;
        assert inputState.getAst() != null;

        int status = EXIT_SUCCESS;
        Logger logger = null;
        SymbolContext symbolContext = null;
        try {
            logger = Logger.getInstance();
            logger.start(inputState.getSourceFile().getName());
            symbolContext = SymbolContext.create();

            var ast = inputState.getAst();
            ast.accept(new GlobalVisitor(symbolContext));
            ast.accept(new ClassVisitor(symbolContext));
            ast.accept(new LocalVisitor(symbolContext));

            if (logger.hasError()) {
                status = EXIT_FAILURE;
                System.err.printf("Static semantic analysis found %d error(s), %d warning(s)%n",
                        logger.getErrorCount(), logger.getWarningCount());
            }

            if (printSymbolTables) {
                symbolContext.dump();
            }
        } catch (final Exception e) {
            status = EXIT_FAILURE;
            e.printStackTrace();
        }

        return CompilerState.builder()
                .status(status)
                .sourceFile(inputState.getSourceFile())
                .scanner(inputState.getScanner())
                .symbolFactory(inputState.getSymbolFactory())
                .ast(inputState.getAst())
                .logger(logger)
                .symbolContext(symbolContext)
                .build();
    }
}
