package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class Formal extends ASTNode{
  public Type t;
  public Identifier i;
  public boolean conflict;
  public semantics.type.Type type;
 
  public Formal(Type at, Identifier ai, Location pos) {
    super(pos);
    t=at; i=ai;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
