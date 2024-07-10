package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.symbol.SymbolTable;

public class Statement extends Stmt {

    private ASTNode<?> stmt;
    private Statement nextStmt;

    public Statement() {
        super(null);
        this.setName("Statement");
    }

    public Statement(Stmt stmt) {
        super(null, stmt);
        this.setName("Statement");
        this.stmt = stmt;
    }

    public Statement(Assignment a) {
        super(null, a);
        this.setName("Statement");
        stmt = a;
    }

    public Statement(FuncCall f) {
        super(null, f);
        stmt = f;
        this.setName("Statement");
    }

    public Statement(ReturnStmt _return) {
        super(null, _return);
        this.setName("Statement");
        stmt = _return;
    }

    public Statement(IfStmt i) {
        super(null, i);
        this.setName("Statement");
        stmt = i;
    }

    public Statement(WhileStmt w) {
        super(null, w);
        this.setName("Statement");
        stmt = w;
    }

    public Statement(Stmt stmt, Statement next) {
        super(null, stmt, next);
        this.setName("Statement");
        this.stmt = stmt;
        this.nextStmt = next;
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        stmt.sem(tbl);
        if (nextStmt != null) {
            nextStmt.sem(tbl);
        }
    }


    public void compile(CompileContext cc) throws CompilerException {
        stmt.compile(cc);

        if (nextStmt != null) {
            nextStmt.compile(cc);
        }
    }
}
