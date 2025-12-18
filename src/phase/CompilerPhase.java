package phase;

public interface CompilerPhase {
    /**
     * Given an input compiler state, runs this phase and outputs the new compiler state
     * @param inputState
     * @return
     */
    CompilerState run(CompilerState inputState);
}
