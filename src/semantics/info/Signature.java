package semantics.info;

import lombok.Getter;
import semantics.table.SymbolContext;
import semantics.type.Type;

import java.util.List;

public final class Signature {
    private String str;
    @Getter
    private final String name;
    @Getter
    private final List<Type> parameters;

    public static Signature of(final String name) {
        return new Signature(name, List.of());
    }

    public static Signature of(final String name, final List<Type> parameters) {
        return new Signature(name, parameters);
    }

    private Signature(final String name, final List<Type> parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        for (int i = 0; i < parameters.size(); i++) {
            if (i == 0) sb.append(SymbolContext.CONSTRUCTOR_POSTFIX);
            sb.append(parameters.get(i));
            if (i < parameters.size() - 1) sb.append(SymbolContext.PARAM_SEPARATOR);
        }
        this.str = sb.toString();
        this.str = this.str.replace("[]", "_$_");
        this.name = name;
        this.parameters = parameters;
    }

    public boolean isAssignableTo(Signature other) {
        if (other.parameters.size() != this.parameters.size()) return false;

        for (int i = 0; i < this.parameters.size(); i++) {
            if (!parameters.get(i).isAssignableTo(other.parameters.get(i))) {
                return false;
            }
        }
        return true;
    }

    public int getSimilarityScore(Signature other) {
        int score = 0;
        for (int i = 0; i < this.parameters.size(); i++) {
            final int sim = parameters.get(i).getSimilarity(other.parameters.get(i));
            if (sim == Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            score += sim;
        }
        return score;
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append('(');
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(parameters.get(i));
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public String toString() {
        return str;
    }
}
