package ast;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class FormalList extends ASTNode {
   private List<Formal> list;

   public FormalList(Location pos) {
      super(pos);
      list = new ArrayList<Formal>();
   }

   public void add(Formal n) {
      list.add(n);
   }

   public Formal get(int i)  { 
      return list.get(i); 
   }

   public int size() { 
      return list.size(); 
   }

   public void forEach(Consumer<Formal> action) {
      for (var f : list) {
         action.accept(f);
      }
   }
}
