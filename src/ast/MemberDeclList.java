package ast;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class MemberDeclList extends ASTNode {
   private List<MemberDecl> list;

   public MemberDeclList(Location pos) {
      super(pos);
      list = new ArrayList<>();
   }

   public void add(MemberDecl n) {
      list.add(n);
   }

   public MemberDecl get(int i)  {
      return list.get(i); 
   }

   public int size() { 
      return list.size(); 
   }

   public void forEach(Consumer<MemberDecl> action) {
      for (var m : list) {
         action.accept(m);
      }
   }
}
