package ast;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class ExpressionList extends ASTNode implements Iterable<Expression> {
   private List<Expression> list;

   public ExpressionList(Location pos) {
      super(pos);
      list = new ArrayList<>();
   }

   public void add(Expression n) {
      list.add(n);
   }

   public Expression get(int i)  {
      return list.get(i); 
   }

   public int size() { 
      return list.size(); 
   }

   public void forEach(Consumer<? super Expression> action) {
      for (var c : list) {
         action.accept(c);
      }
   }

   @Override
   public Iterator<Expression> iterator() {
      return list.iterator();
   }
}
