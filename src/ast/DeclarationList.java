package ast;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class DeclarationList extends ASTNode {
   private final List<Declaration> list;

   public DeclarationList(Location pos) {
      super(pos);
      list = new ArrayList<>();
   }

   public void add(Declaration n) {
      list.add(n);
   }

   public Declaration get(int i)  {
      return list.get(i); 
   }

   public int size() { 
      return list.size(); 
   }

   public void forEach(Consumer<Declaration> action) {
      for (var v : list) {
         action.accept(v);
      }
   }
}
