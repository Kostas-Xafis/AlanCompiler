package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;

public class Cond extends Expr<DataType> {

    public Cond(Ops op) {
        super(DataType.Bool(), null, op);
    }

    public Cond(ConstBool bool) {
        super(DataType.Bool(), null, bool);
    }

    public Cond(Lops lop) {
        super(DataType.Bool(), null, lop);
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        ASTNode<?> left = getLeft();
        if (left == null) {
            throw new SemanticException("Left operand of condition is null");
        }
        if (left instanceof Ops) {
            ((Ops) left).sem(tbl);
        } else if (left instanceof Lops) {
            ((Lops) left).sem(tbl);
        } else if (left instanceof ConstBool) {
            ((ConstBool) left).sem(tbl);
        }
    }

    @Override
    public String toString() {
        return "Cond(" + getValue() + ")";
    }
}
