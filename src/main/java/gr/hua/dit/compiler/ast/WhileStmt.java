package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.symbol.SymbolTable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

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

    public void compile(CompileContext cc) throws CompilerException {
        // Create a new label for the start of the loop
        // Compile the condition
        LabelNode start = new LabelNode();
        LabelNode end = new LabelNode();
        cc.addInsn(start);
        cc.setExitLabel(end);
        cond.compile(cc);
        stmt.compile(cc);
        cc.addInsn(new JumpInsnNode(Opcodes.GOTO, start));
        cc.addInsn(end);
    }
}
