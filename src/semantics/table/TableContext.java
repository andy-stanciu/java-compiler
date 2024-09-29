package semantics.table;

import semantics.info.ClassInfo;
import semantics.info.MethodInfo;

public record TableContext(SymbolTable table, ClassInfo currentClass,
                           MethodInfo currentMethod) {}
