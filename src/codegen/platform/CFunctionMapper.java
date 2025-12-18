package codegen.platform;

import java.util.HashMap;
import java.util.Map;

public final class CFunctionMapper {
    private static final Map<CFunction, Label> cFunctions = new HashMap<>();

    static {
        cFunctions.put(CFunction.MALLOC, Label.of("jcalloc"));
        cFunctions.put(CFunction.MEMCOPY, Label.of("jmemcpy"));
        cFunctions.put(CFunction.PRINT, Label.of("put"));
        cFunctions.put(CFunction.PRINTC, Label.of("put_char"));
        cFunctions.put(CFunction.PRINTB, Label.of("put_bool"));
    }

    public static Label map(CFunction func) {
        if (!cFunctions.containsKey(func)) {
            throw new IllegalArgumentException();
        }
        return cFunctions.get(func);
    }
}
