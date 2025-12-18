package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

abstract public class ASTNode {
  public final int lineNumber;

  public ASTNode(Location pos) {
    this.lineNumber = pos.getLine();
  }
}
