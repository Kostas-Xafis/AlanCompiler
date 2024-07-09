package gr.hua.dit.compiler.types;

import gr.hua.dit.compiler.symbol.SymbolTable;

public abstract class Type {

    public void setRef(Boolean ref) {
        throw new UnsupportedOperationException();
    }

    public void setArray(Integer arrSize) {
        throw new UnsupportedOperationException();
    }

    public boolean equals(Type t) {
        throw new UnsupportedOperationException();
    }

    public void typeCheck(SymbolTable tbl, Type t) {
        throw new UnsupportedOperationException();
    }

    public Type copy() {
        throw new UnsupportedOperationException();
    }
}
