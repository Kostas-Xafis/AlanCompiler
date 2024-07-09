package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.symbol.SymbolTable;

public abstract class Stmt extends ASTNode<Object> {
    public Stmt(Object value, ASTNode<?> left, ASTNode<?> right) {
        super("Stmt", value, left, right);
    }

    public Stmt(Object value, ASTNode<?> left) {
        super("Stmt", value, left);
    }

    public Stmt(Object value) {
        super("Stmt", value);
    }

    public Stmt() {
        super("Stmt");
    }

    public abstract void sem(SymbolTable tbl) throws SemanticException;
}
