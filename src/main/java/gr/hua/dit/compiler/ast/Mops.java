package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;

public class Mops extends Expr<DataType> {

    public enum Operator {
        ADD("+"),
        SUB("-"),
        MUL("*"),
        DIV("/"),
        MOD("%"),
        PLUS_SIGN("+"),
        MINUS_SIGN("-");
        private final String value;
        Operator(String value) {
            this.value = value;
        }
        public String toString() {
            return value;
        }
    }

    public Operator op;
    private final Expr<?> l, r;
    public Mops(Operator o, Expr<?> l, Expr<?> r) {
        super(DataType.Int(), o.toString(), l, r);
        this.op = o;
        this.l = l;
        this.r = r;
    }

    public Mops(Operator o, Expr<?> l) {
        this(o, l, null);
    }

    public String toString() {
        return "Mops(" + l + op + (r != null ? r.toString() : "") + ")";
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        if (l != null) l.typeCheck(tbl, DataType.IntType);
        if (r != null) r.typeCheck(tbl, DataType.IntType);
    }
}
