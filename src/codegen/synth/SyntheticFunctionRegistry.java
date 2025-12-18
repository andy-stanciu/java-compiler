package codegen.synth;

import codegen.platform.*;
import codegen.platform.isa.ISA;
import codegen.synth.def.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class SyntheticFunctionRegistry {
    private static SyntheticFunctionRegistry instance;

    private final Map<SyntheticFunction, SyntheticFunctionDefinition> registry;
    private final ISA isa;

    public static SyntheticFunctionRegistry getInstance(final ISA isa) {
        if (instance == null) {
            instance = new SyntheticFunctionRegistry(isa);
        }
        return instance;
    }

    private SyntheticFunctionRegistry(final ISA isa) {
        this.registry = new HashMap<>();
        this.isa = isa;
        registerAll();
    }

    private void registerAll() {
        register(new AllocateArray(isa));
        register(new AllocateNestedArray(isa));
        register(new ConcatStrings(isa));
        register(new ConcatStringBool(isa));
        register(new ConcatBoolString(isa));
        register(new ConcatStringInt(isa));
        register(new ConcatIntString(isa));
        register(new LoadStringTrue(isa));
        register(new LoadStringFalse(isa));
        register(new LoadStringInt(isa));
    }

    private void register(final SyntheticFunctionDefinition definition) {
        if (registry.containsKey(definition.getName())) {
            throw new IllegalArgumentException();
        }
        registry.put(definition.getName(), definition);
    }

    public Label getSyntheticFunctionLabel(final SyntheticFunction syntheticFunction) {
        if (!registry.containsKey(syntheticFunction)) {
            throw new IllegalArgumentException();
        }

        var function = registry.get(syntheticFunction);
        function.setReferenced(true);
        return registry.get(syntheticFunction).getLabel();
    }

    /**
     * Generates all the synthetic functions that have been referenced.
     */
    public void generateAll() {
        Set<SyntheticFunction> generated = new HashSet<>();
        int initialSize;
        do {
            initialSize = generated.size();
            registry.forEach((function, definition) -> {
                if (definition.isReferenced() && !generated.contains(function)) {
                    generated.add(function);
                    definition.generateFunction();
                }
            });
        } while (generated.size() != initialSize);
    }
}
