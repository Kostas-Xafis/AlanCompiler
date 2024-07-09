package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.symbol.SymbolTable;

public class IfStmt extends Stmt {
    private Cond cond;
    private IfStmt if_stmt;
    private Stmt then_stmt;
    private Stmt else_stmt;

    public IfStmt(Stmt s1, Stmt s2) {
        super(null);
        this.setName("IfStmt");
        this.then_stmt = s1;
        this.else_stmt = s2;
    }

    public IfStmt(Cond c, IfStmt _if) {
        super(null);
        this.setName("IfStmt");
        this.cond = c;
        this.if_stmt = _if;
    }

    public IfStmt(Stmt s1) {
        this(s1, null);
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        if (cond != null) cond.sem(tbl);
        if (if_stmt != null) if_stmt.sem(tbl);
        if (then_stmt != null) then_stmt.sem(tbl);
        if (else_stmt != null) else_stmt.sem(tbl);
    }

    public void codegen() {
//        LabelAddress labelTrue = IRHelper.newLabel();
//        LabelAddress labelFalse = IRHelper.newLabel();
//        cond.codegen();
//        System.out.println("if " + cond.getAddress() + " goto " + labelTrue);
//        System.out.println("goto " + labelFalse);
//        System.out.println(labelTrue + ":");
//        then_stmt.codegen();
//        System.out.println(labelTrue + ":");
    }
}
