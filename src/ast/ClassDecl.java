package ast;

import ast.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class ClassDecl extends ASTNode{
  public ClassDecl(Location pos) {
    super(pos);
  }
  public abstract void accept(Visitor v);
}
