package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

import java.util.HashSet;
import java.util.Set;

public abstract class Statement extends ASTNode {
    public final Set<String> used;
    public final Set<String> defined;

    public Statement(Location pos) {
        super(pos);
        used = new HashSet<>();
        defined = new HashSet<>();
    }
    public abstract void accept(Visitor v);
}
