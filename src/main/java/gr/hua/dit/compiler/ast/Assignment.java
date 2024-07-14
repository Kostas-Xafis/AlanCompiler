package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.errors.TypeException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.VarInsnNode;

public class Assignment extends ASTNode {
    private final LValue lvalue;
    private final Expr<?> expr;

    public Assignment(LValue lvalue, Expr<?> expr) {
        super("Assignment", null, lvalue, expr);
        this.lvalue = lvalue;
        this.expr = expr;
    }

    public String toString() {
        return "Assignment(" + lvalue + "," + expr + ")";
    }

    public Expr<?> getExpr() {
        return expr;
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        lvalue.sem(tbl);
        expr.sem(tbl);
        if (expr instanceof FuncCall) {
            FuncCall fc = (FuncCall) expr;
            if (fc.getInferredType().getResult().equals(DataType.ProcType)) {
                throw new SemanticException("Function call in assignment does not return a value");
            } else if (!lvalue.getInferredType().equals(fc.getInferredType().getResult())) {
                throw new TypeException("Assignment", fc.getInferredType().getResult(), lvalue.getInferredType());
            }
        } else if (!lvalue.getInferredType().equals(expr.getInferredType())) {
            throw new TypeException("Assignment", lvalue.getInferredType(), expr.getInferredType());
        }
    }

    public void compile(CompileContext cc) throws CompilerException {
        if(lvalue.getInferredType().isAccessed()) {
            lvalue.compile(cc, "store", expr);
        } else {
            expr.compile(cc);
            lvalue.compile(cc, "store");
        }
    }
}
