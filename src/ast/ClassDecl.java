package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class ClassDecl extends ASTNode {
  public boolean conflict;

  public ClassDecl(Location pos) {
    super(pos);
  }

  public abstract void accept(Visitor v);
}
