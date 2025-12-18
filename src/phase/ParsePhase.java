package phase;

import ast.Program;
import parser.parser;

import static phase.CompilerState.EXIT_FAILURE;
import static phase.CompilerState.EXIT_SUCCESS;

public final class ParsePhase implements CompilerPhase {
    @Override
    public CompilerState run(CompilerState inputState) {
        assert inputState.getStatus() != EXIT_FAILURE;
        assert inputState.getScanner() != null;
        assert inputState.getSymbolFactory() != null;

        int status = EXIT_SUCCESS;
        Program ast = null;
        try {
            var p = new parser(inputState.getScanner(), inputState.getSymbolFactory());
            ast = (Program) p.parse().value;
        } catch (final Exception e) {
            status = EXIT_FAILURE;
            e.printStackTrace();
        }

        return CompilerState.builder()
                .status(status)
                .sourceFile(inputState.getSourceFile())
                .scanner(inputState.getScanner())
                .symbolFactory(inputState.getSymbolFactory())
                .ast(ast)
                .build();
    }
}
