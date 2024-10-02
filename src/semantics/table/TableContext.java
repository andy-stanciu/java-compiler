package semantics.table;

import semantics.info.BlockInfo;
import semantics.info.ClassInfo;
import semantics.info.MethodInfo;

import java.util.Stack;

public record TableContext(SymbolTable table, ClassInfo currentClass,
                           MethodInfo currentMethod, Stack<BlockInfo> currentBlocks) {}
