package ast;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class VarDeclarationList extends ASTNode {
   private final List<VarDeclaration> list;

   public VarDeclarationList(Location pos) {
      super(pos);
      list = new ArrayList<>();
   }

   public void add(VarDeclaration n) {
      list.add(n);
   }

   public VarDeclaration get(int i)  {
      return list.get(i); 
   }

   public int size() { 
      return list.size(); 
   }

   public void forEach(Consumer<VarDeclaration> action) {
      for (var v : list) {
         action.accept(v);
      }
   }
}
