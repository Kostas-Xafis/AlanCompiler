package gr.hua.dit.compiler.symbol;

import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

public class SymbolTable {

    private static class Scope extends HashMap<String, SymbolEntry> {
        public Scope(){
            super();
        }
    }

    private final Deque<Scope> scopes;


    public SymbolTable() {
        scopes = new LinkedList<>();
        openScope();
    }

    public Optional<SymbolEntry> lookup(String sym, Boolean shallow) {
        if (shallow) {
            return Optional.ofNullable(lookupShallow(sym));
        } else {
            return Optional.ofNullable(lookupFull(sym));
        }
    }

    public Optional<SymbolEntry> lookup(String sym) {
        return lookup(sym, false);
    }

    public SymbolEntry lookupShallow(String sym){
        return scopes.getFirst().get(sym);
    }

    // recurse through all scopes
    public SymbolEntry lookupFull(String sym) {
        // first scope in the list in the most recent
        for (Scope s : scopes) {
            SymbolEntry e = s.get(sym);
            if (e != null)
                return e;
        }
        return null;
    }

    public void addEntry(String sym, Type t) {
        Scope s = scopes.getFirst();
        s.put(sym, new SymbolEntry(sym, t));
    }

    public void openScope() {
        scopes.addFirst(new Scope());
    }

    public void closeScope() {
        scopes.removeFirst();
    }

    public SymbolEntry getFunctionEntry() {
        Scope currScope = scopes.removeFirst();
        SymbolEntry s = scopes.getFirst().values().stream().filter(x -> x.getType() instanceof FuncType).findFirst().orElse(null);
        scopes.addFirst(currScope);
        return s;
    }
}
