package codegen.synth;

import lombok.Getter;

@Getter
public enum SyntheticFunction {
    ALLOCATE_ARRAY("alloc_arr"),
    ALLOCATE_NESTED_ARRAY("alloc_nested_arr"),
    CONCAT_STRING_STRING("concat_string_string"),
    CONCAT_STRING_BOOL("concat_string_bool"),
    CONCAT_BOOL_STRING("concat_bool_string"),
    CONCAT_STRING_INT("concat_string_int"),
    CONCAT_INT_STRING("concat_int_string"),
    LOAD_STRING_TRUE("load_string_true"),
    LOAD_STRING_FALSE("load_string_false"),
    LOAD_STRING_INT("load_string_int"),
    ;

    private final String label;

    SyntheticFunction(final String label) {
        this.label = label;
    }
}
