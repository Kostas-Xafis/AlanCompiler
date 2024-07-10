package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.irgen.IRHelper;
import gr.hua.dit.compiler.irgen.LabelAddress;
import gr.hua.dit.compiler.symbol.SymbolTable;

public class WhileStmt extends Stmt {
    private final Cond cond;
    private final Stmt stmt;

    public WhileStmt(Cond c, Stmt s) {
        super(null, c, s);
        this.setName("WhileStmt");
        this.cond = c;
        this.stmt = s;
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        cond.sem(tbl);
        stmt.sem(tbl);
    }

    public void compile() {
        LabelAddress labelTrue = IRHelper.newLabel();
        LabelAddress labelFalse = IRHelper.newLabel();
        System.out.println(labelTrue + ":");
//        cond.compile();
        System.out.println("if " + cond.getAddress() + " goto " + labelTrue);
        System.out.println("goto " + labelFalse);
        System.out.println(labelFalse + ":");
    }
}
