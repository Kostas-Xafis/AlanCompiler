package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;

public class Lops extends Expr<DataType> {

    public enum Operator {
        OR("||"),
        AND("&&"),
        NOT("!");
        private final String value;
        Operator(String value) {
            this.value = value;
        }
        public String toString() {
            return value;
        }
    };

    private Operator op;
    private Expr<?> l, r;
    private Cond c;

    public Lops(Operator o, Expr<?> l, Expr<?> r) {
        super(DataType.Bool(), o.toString(), l, r);
        this.op = o;
        this.l = l;
        this.r = r;
        this.setName("Lops");
    }

    public Lops(Operator o, Cond c) {
        super(DataType.Bool(), o.toString(), c, null);
        this.c = c;
        this.setName("Lops");
    }

    public String toString() {
        return op + "(" + l + "," + r + ")";
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        if (l != null) l.typeCheck(tbl, DataType.BoolType);
        if (r != null) r.typeCheck(tbl, DataType.BoolType);
        if (c != null) c.typeCheck(tbl, DataType.BoolType);
    }
}
