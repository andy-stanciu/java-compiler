package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class IdentifierType extends Type {
  public String s;

  public IdentifierType(String as, Location pos) {
    super(pos);
    s=as;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
