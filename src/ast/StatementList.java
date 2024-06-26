package ast;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class StatementList extends ASTNode implements Iterable<Statement> {
   private List<Statement> list;

   public StatementList(Location pos) {
      super(pos);
      list = new ArrayList<Statement>();
   }

   public void add(Statement n) {
      list.add(n);
   }

   public Statement get(int i)  { 
      return list.get(i); 
   }

   public int size() { 
      return list.size(); 
   }

   public void forEach(Consumer<? super Statement> action) {
      for (var s : list) {
         action.accept(s);
      }
   }

   @Override
   public Iterator<Statement> iterator() {
      return list.iterator();
   }
}
