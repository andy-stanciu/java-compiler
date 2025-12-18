package phases;

import ast.visitor.codegen.CodeDataVisitor;
import ast.visitor.codegen.CodeGenVisitor;

import static phases.CompilerState.EXIT_FAILURE;
import static phases.CompilerState.EXIT_SUCCESS;

public final class CodeGenerationPhase implements CompilerPhase {
    @Override
    public CompilerState run(final CompilerState inputState) {
        assert inputState.getStatus() != EXIT_FAILURE;
        assert inputState.getAst() != null;
        assert inputState.getSymbolContext() != null;
        assert inputState.getIsa() != null;

        int status = EXIT_SUCCESS;
        try {
            var ast = inputState.getAst();
            ast.accept(new CodeDataVisitor(inputState.getSymbolContext(), inputState.getIsa()));
            ast.accept(new CodeGenVisitor(inputState.getSymbolContext(), inputState.getIsa()));
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
                .dataflowVisitor(inputState.getDataflowVisitor())
                .isa(inputState.getIsa())
                .build();
    }
}
