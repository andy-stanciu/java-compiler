package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class And extends Exp {
  public Exp e1,e2;
  
  public And(Exp ae1, Exp ae2, Location pos) {
    super(pos);
    e1=ae1; e2=ae2;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
