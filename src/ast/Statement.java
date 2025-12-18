package ast;

import commons.Visitor;
import dataflow.Symbol;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.info.MethodInfo;

import java.util.HashSet;
import java.util.Set;

public abstract class Statement extends ASTNode {
    public final Set<Symbol> used;
    public final Set<Symbol> defined;
    public MethodInfo method;

    public Statement(Location pos) {
        super(pos);
        used = new HashSet<>();
        defined = new HashSet<>();
    }
    public abstract void accept(Visitor v);
}
