package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;

public class Ops extends Expr {

    public enum Operator {
        EQ("=="),
        NE("!="),
        LT("<"),
        GT(">"),
        LE("<="),
        GE(">=");

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

    public Ops(Operator o, Expr<?> l, Expr<?> r) {
        super(DataType.Bool(), o.toString(), l, r);
        this.op = o;
        this.l = l;
        this.r = r;
    }

    public String toString() {
        return op.toString();
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        if (l == null || r == null) {
            throw new SemanticException("Left or right operand of Ops is missing");
        }
        l.sem(tbl);
        r.sem(tbl);
        l.typeCheckMatch(tbl, r);
    }
}
