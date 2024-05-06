package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class False extends Exp {
  public False(Location pos) {
    super(pos);
  }
  public void accept(Visitor v) {
    v.visit(this);
  }
}
