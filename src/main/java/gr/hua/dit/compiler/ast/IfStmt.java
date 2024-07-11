package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.symbol.SymbolTable;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

public class IfStmt extends Stmt {
    private Cond cond;
    private Stmt if_body;
    private IfStmt else_stmt;

    public IfStmt(Stmt s1, IfStmt s2) {
        super(null, s1, s2);
        System.out.println("IfStmt init");
        this.setName("IfStmt");
        this.if_body = s1;
        this.else_stmt = s2;
    }

    public IfStmt(Stmt s1) {
        super(null, null, s1);
        System.out.println("IfStmtBody init");
        this.setName("IfStmtBody");
        this.if_body = s1;
    }

    public void setCond(Cond c) {
        this.cond = c;
        this.setLeft(c);
    }


    public void sem(SymbolTable tbl) throws SemanticException {
        if (cond != null) cond.sem(tbl);
        if (if_body != null) if_body.sem(tbl);
        if (else_stmt != null) else_stmt.sem(tbl);
    }

    public void compile(CompileContext cc) throws CompilerException {
        LabelNode prevExitLabel = cc.getExitLabel();
        LabelNode labelTrue = CompileContext.newLabel();
        LabelNode labelFalse = prevExitLabel != null && else_stmt == null ? prevExitLabel : CompileContext.newLabel();

        cc.setExitLabel(labelFalse);

        if (cond != null) {
            System.out.println("if " + cond);
            cond.compile(cc);
        }

        if (if_body != null) {
            if_body.compile(cc);
        }
        if (else_stmt != null) {
            LabelNode exitLabel = prevExitLabel != null ? prevExitLabel : labelTrue;
            cc.addInsn(new JumpInsnNode(Opcodes.GOTO, exitLabel));
            cc.setExitLabel(exitLabel);
            cc.addInsn(labelFalse);
            else_stmt.compile(cc);
            if (cond != null && (else_stmt.cond != null || prevExitLabel == null)) {
                cc.addInsn(exitLabel);
            }
        }
        cc.setExitLabel(prevExitLabel);
    }
}
