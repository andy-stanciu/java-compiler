package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class IntArrayType extends Type {
  public IntArrayType(Location pos) {
    super(pos);
  }
  public void accept(Visitor v) {
    v.visit(this);
  }
}
