package phase;

import dataflow.visitor.DataflowVisitor;

import static phase.CompilerState.EXIT_FAILURE;
import static phase.CompilerState.EXIT_SUCCESS;

public final class DataflowAnalysisPhase implements CompilerPhase {
    private final boolean printInstructions;
    private final boolean printBlocks;

    public DataflowAnalysisPhase() {
        this(false, false);
    }

    public DataflowAnalysisPhase(final boolean printInstructions,
                                 final boolean printBlocks) {
        this.printInstructions = printInstructions;
        this.printBlocks = printBlocks;
    }

    @Override
    public CompilerState run(final CompilerState inputState) {
        assert inputState.getStatus() != EXIT_FAILURE;
        assert inputState.getAst() != null;
        assert inputState.getSymbolContext() != null;
        assert inputState.getLogger() != null;

        int status = EXIT_SUCCESS;
        DataflowVisitor dataflowVisitor = null;
        try {
            var logger = inputState.getLogger();
            logger.restart();
            dataflowVisitor = new DataflowVisitor(inputState.getSymbolContext());
            inputState.getAst().accept(dataflowVisitor);

            if (logger.hasError()) {
                status = EXIT_FAILURE;
                System.err.printf("Dataflow analysis found %d error(s), %d warning(s)%n",
                        logger.getErrorCount(), logger.getWarningCount());
            }

            if (printInstructions) {
                dataflowVisitor.dumpInstructions();
            }
            if (printBlocks) {
                dataflowVisitor.dumpBlocks();
            }
        } catch (final Exception e) {
            status = EXIT_FAILURE;
            e.printStackTrace();
        }

        return CompilerState.builder()
                .status(status)
                .sourceFile(inputState.getSourceFile())
                .scanner(inputState.getScanner())
                .ast(inputState.getAst())
                .logger(inputState.getLogger())
                .symbolContext(inputState.getSymbolContext())
                .dataflowVisitor(dataflowVisitor)
                .build();
    }
}
