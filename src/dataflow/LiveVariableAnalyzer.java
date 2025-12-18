package dataflow;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public final class LiveVariableAnalyzer {
    private final Set<Symbol> useSet;
    private final Set<Symbol> defSet;
    private final Set<Symbol> inSet;
    private final Set<Symbol> outSet;

    public LiveVariableAnalyzer() {
        this.useSet = new HashSet<>();
        this.defSet = new HashSet<>();
        this.inSet = new HashSet<>();
        this.outSet = new HashSet<>();
    }
}
