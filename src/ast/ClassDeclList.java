package ast;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class ClassDeclList extends ASTNode{
   private List<ClassDecl> list;

   public ClassDeclList(Location pos) {
      super(pos);
      list = new ArrayList<ClassDecl>();
   }

   public void add(ClassDecl n) {
      list.add(n);
   }

   public ClassDecl get(int i)  { 
      return list.get(i); 
   }

   public int size() { 
      return list.size(); 
   }

   public void forEach(Consumer<ClassDecl> action) {
      for (var c : list) {
         action.accept(c);
      }
   }
}
