package codegen.platform;

import java.util.HashMap;
import java.util.Map;

public final class CFunctionMapper {
    private static final Map<CFunction, Label> cFunctions = new HashMap<>();

    static {
        cFunctions.put(CFunction.MALLOC, Label.of("mjcalloc"));
        cFunctions.put(CFunction.PRINT, Label.of("put"));
    }

    public static Label map(CFunction func) {
        if (!cFunctions.containsKey(func)) {
            throw new IllegalArgumentException();
        }
        return cFunctions.get(func);
    }
}
