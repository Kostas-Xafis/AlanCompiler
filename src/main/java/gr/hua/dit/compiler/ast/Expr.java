package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.errors.TypeException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;

public class Expr<T extends Type> extends ASTNode {

    private T inferredType;

    public Expr(T type, Object value, ASTNode<?> left, ASTNode<?> right) {
        super("Expr", value, left, right);
        this.inferredType = type;
    }

    public Expr(T type, Object value, ASTNode<?> left) {
        this(type, value, left, null);
    }

    public Expr(T type, Object value) {
        this(type, value, null, null);
    }

    public Expr(T type) {
        this(type, null, null, null);
    }

    // check if expression is well-typed
    public void typeCheck(SymbolTable tbl, Type t) throws SemanticException {
        // analysis will populate the inferred type of Expr
        sem(tbl);
        if (!this.inferredType.equals(t)) {
            throw new TypeException(t, this.inferredType);
        }
    }


    private void typeToTypeCheck(Type t1, Type t2) throws SemanticException {
        if (!t1.equals(t2)) {
            throw new TypeException(t1, t2);
        }
    }

    private void funcToTypeCheck(FuncType f, Type t) throws SemanticException {
        if (f.getResult().equals(DataType.ProcType)) {
            throw new TypeException("Function call in assignment does not return a value");
        }
        if (!f.getResult().equals(t)) {
            throw new TypeException("Function call", f.getResult(), t);
        }
    }

    private void funcToFuncCheck(FuncType f, FuncType t) throws SemanticException {
        if (!f.resultEquals(t)) {
            throw new TypeException("Function call", f, t);
        }
    }

    public void typeCheckMatch(SymbolTable tbl, Expr<?> node) throws SemanticException {
        // It should be called after the semantic analysis
        if (this.inferredType instanceof FuncType) {
            if (node.getInferredType() instanceof FuncType) {
                funcToFuncCheck((FuncType) this.inferredType, (FuncType) node.getInferredType());
            } else {
                funcToTypeCheck((FuncType) this.inferredType, node.getInferredType());
            }
        } else {
            typeToTypeCheck(this.getInferredType(), node.getInferredType());
        }
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        // do semantic analysis in this node
    }

    public void compile(CompileContext cc) throws CompilerException {
        // do compilation in this node
    }

    public T getInferredType() {
        return inferredType;
    }

    public void setInferredType(T t) {
        this.inferredType = (T) t.copy();
    }
}
