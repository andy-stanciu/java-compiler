package phases;

import java_cup.runtime.ComplexSymbolFactory;
import parser.sym;
import scanner.scanner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import static phases.CompilerState.EXIT_FAILURE;
import static phases.CompilerState.EXIT_SUCCESS;

public final class ScanPhase implements CompilerPhase {
    private final boolean printTokens;

    public ScanPhase() {
        this(false);
    }

    public ScanPhase(final boolean printTokens) {
        this.printTokens = printTokens;
    }

    @Override
    public CompilerState run(final CompilerState inputState) {
        assert inputState.getStatus() != EXIT_FAILURE;
        assert inputState.getSourceFile() != null;

        int status = EXIT_SUCCESS;
        ComplexSymbolFactory symbolFactory = null;
        scanner scanner = null;

        try {
            symbolFactory = new ComplexSymbolFactory();
            var in = new BufferedReader(new InputStreamReader(new FileInputStream(inputState.getSourceFile())));
            scanner = new scanner(in, symbolFactory);

            if (printTokens) {
                var t = scanner.next_token();
                while (t.sym != sym.EOF) {
                    System.out.print(scanner.symbolToString(t) + " ");
                    if (t.sym == sym.error) {
                        status = EXIT_FAILURE;
                    }
                    t = scanner.next_token();
                }
            }
        } catch (final Exception e) {
            status = EXIT_FAILURE;
            e.printStackTrace();
        }

        return CompilerState.builder()
                .status(status)
                .sourceFile(inputState.getSourceFile())
                .symbolFactory(symbolFactory)
                .scanner(scanner)
                .build();
    }
}
