package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.Type;

import java.util.ArrayList;

public class ExprList extends ASTNode {

    private final Expr<?> expr;
    private ExprList next;

    public ExprList(Expr<?> e, ExprList next) {
        super("ExprList", null, e, next);
        this.expr = e;
        if (next != null) {
            this.next = next;
        }
    }

    public ExprList(Expr<?> e) {
        this(e, null);
    }

    public ArrayList<Type> getTypeList(ArrayList<Type> typeArr) {
        if (typeArr == null) {
            typeArr = new ArrayList<>();
        }
        typeArr.add(this.expr.getInferredType());
        if (next != null) {
            next.getTypeList(typeArr);
        }
        return typeArr;
    }

    @Override
    public String toString() {
        return "ExprList(" + expr + (next != null ? ", " + next : "") + ")";
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        expr.sem(tbl);
        if (next != null) {
            next.sem(tbl);
        }
    }
}
