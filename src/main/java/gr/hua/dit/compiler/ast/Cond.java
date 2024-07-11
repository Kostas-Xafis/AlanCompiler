package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;
import org.objectweb.asm.tree.LabelNode;

public class Cond extends Expr<DataType> {

    public Cond(Ops op) {
        super(DataType.Bool(), null, op);
        this.setName("Cond");
    }

    public Cond(ConstBool bool) {
        super(DataType.Bool(), null, bool);
        this.setName("Cond");
    }

    public Cond(Lops lop) {
        super(DataType.Bool(), null, lop);
        this.setName("Cond");
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

    public void compile(CompileContext cc) throws CompilerException {
        ASTNode<?> left = getLeft();
        LabelNode subCondLabel = CompileContext.newLabel();
        if (left instanceof Ops) {
            ((Ops) left).compile(cc);
        } else if (left instanceof Lops) {
            ((Lops) left).compile(cc);
        } else if (left instanceof ConstBool) {
            ((ConstBool) left).compile(cc);
        }
//        cc.addInsn(subCondLabel);
    }

    @Override
    public String toString() {
        return "Cond(" + getLeft() + ")";
    }
}
