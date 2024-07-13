package dataflow;

import java.util.HashSet;
import java.util.Set;

public final class LiveVariableAnalyzer {
    private final Set<String> use;
    private final Set<String> def;
    private final Set<String> in;
    private final Set<String> out;

    public LiveVariableAnalyzer() {
        this.use = new HashSet<>();
        this.def = new HashSet<>();
        this.in = new HashSet<>();
        this.out = new HashSet<>();
    }

    public Set<String> useSet() {
        return use;
    }

    public Set<String> defSet() {
        return def;
    }

    public Set<String> inSet() {
        return in;
    }

    public Set<String> outSet() {
        return out;
    }
}
