package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CaseList extends ASTNode {
    private List<Case> list;

    public CaseList(Location pos) {
        super(pos);
        list = new ArrayList<>();
    }

    public void add(Case n) {
        list.add(n);
    }

    public Case get(int i)  {
        return list.get(i);
    }

    public int size() {
        return list.size();
    }

    public void forEach(Consumer<Case> action) {
        for (var c : list) {
            action.accept(c);
        }
    }
}
