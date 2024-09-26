package ast;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class ExpList extends ASTNode implements Iterable<Exp> {
   private List<Exp> list;

   public ExpList(Location pos) {
      super(pos);
      list = new ArrayList<Exp>();
   }

   public void add(Exp n) {
      list.add(n);
   }

   public Exp get(int i)  { 
      return list.get(i); 
   }

   public int size() { 
      return list.size(); 
   }

   public void forEach(Consumer<? super Exp> action) {
      for (var c : list) {
         action.accept(c);
      }
   }

   @Override
   public Iterator<Exp> iterator() {
      return list.iterator();
   }
}
