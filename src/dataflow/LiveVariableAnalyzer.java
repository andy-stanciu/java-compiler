package dataflow;

import java.util.HashSet;
import java.util.Set;

public final class LiveVariableAnalyzer {
    private final Set<Symbol> use;
    private final Set<Symbol> def;
    private final Set<Symbol> in;
    private final Set<Symbol> out;

    public LiveVariableAnalyzer() {
        this.use = new HashSet<>();
        this.def = new HashSet<>();
        this.in = new HashSet<>();
        this.out = new HashSet<>();
    }

    public Set<Symbol> useSet() {
        return use;
    }

    public Set<Symbol> defSet() {
        return def;
    }

    public Set<Symbol> inSet() {
        return in;
    }

    public Set<Symbol> outSet() {
        return out;
    }
}
