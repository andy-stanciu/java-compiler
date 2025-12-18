package phase;

import java.util.LinkedList;
import java.util.List;

import static phase.CompilerState.EXIT_FAILURE;

public final class CompilerPhaseChain {
    private final List<CompilerPhase> phases;

    public CompilerPhaseChain() {
        this.phases = new LinkedList<>();
    }

    public CompilerPhaseChain add(final CompilerPhase phase) {
        phases.add(phase);
        return this;
    }

    /**
     * Runs all the compiler phases, returning the final compiler state.
     * @param initialState Initial compiler state.
     */
    public CompilerState run(final CompilerState initialState) {
        if (phases.isEmpty()) {
            return initialState;
        }

        var currentState = initialState;
        for (final var phase : phases) {
            var nextState = phase.run(currentState);
            if (nextState.getStatus() == EXIT_FAILURE) {
                return nextState;
            }
            currentState = nextState;
        }
        return currentState;
    }
}
