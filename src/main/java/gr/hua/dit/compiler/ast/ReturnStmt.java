package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.errors.TypeException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;

public class ReturnStmt extends ASTNode {
    private final Expr<?> expr;

    public ReturnStmt(Expr<?> expr) {
        super("ReturnStmt", null, expr);
        this.expr = expr;
    }

    public String toString() {
        return "ReturnStmt(" + expr + ")";
    }

    public Expr<?> getExpr() {
        return expr;
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        // Check if the return type of the function matches the return type of the expression
        FuncType e = (FuncType) tbl.getFunctionEntry().getType();
        Type funcDefReturnType = e.getResult();
        if (expr != null) {
            expr.sem(tbl);
            Type returnType;
            if (expr.getInferredType() instanceof FuncType) {
                returnType = ((FuncType) expr.getInferredType()).getResult();
            } else {
                returnType = expr.getInferredType();
            }
            if (!funcDefReturnType.equals(returnType)) {
                throw new TypeException("ReturnStmt", funcDefReturnType, returnType);
            }
        } else if (!funcDefReturnType.equals(DataType.ProcType)) {
            throw new TypeException("ReturnStmt", funcDefReturnType, DataType.ProcType);
        }
    }

    public void compile(CompileContext cc) throws CompilerException {
        if (expr != null) {
            expr.compile(cc);
        }
    }
}
