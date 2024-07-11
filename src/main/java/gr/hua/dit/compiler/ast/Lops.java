package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

public class Lops extends Expr<DataType> {

    public enum Operator {
        OR("||"),
        AND("&&"),
        NOT("!");
        private final String value;
        Operator(String value) {
            this.value = value;
        }
        public String toString() {
            return value;
        }
    };

    private Operator op;
    private Expr<?> l, r;
    private Cond c;

    public Lops(Operator o, Expr<?> l, Expr<?> r) {
        super(DataType.Bool(), o.toString(), l, r);
        this.setName("Lops");
        this.op = o;
        this.l = l;
        this.r = r;
    }

    public Lops(Operator o, Cond c) {
        super(DataType.Bool(), o.toString(), c, null);
        this.c = c;
        this.setName("Lops");
    }

    public String toString() {
        return op + "(" + l + "," + r + ")";
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        if (l != null) l.typeCheck(tbl, DataType.BoolType);
        if (r != null) r.typeCheck(tbl, DataType.BoolType);
        if (c != null) c.typeCheck(tbl, DataType.BoolType);
    }

    // Implement logical operators with short-circuiting behavior
    public void compile(CompileContext cc) throws CompilerException {
        LabelNode prevExitLabel = cc.getExitLabel();
        LabelNode shortCircuit;
        switch (op) {
            case OR:
                shortCircuit = CompileContext.newLabel();
                cc.invertIfStmtLogic();
                cc.setExitLabel(shortCircuit);
                if (l != null) l.compile(cc);
                cc.invertIfStmtLogic();
                cc.setExitLabel(prevExitLabel);
                if (r != null) r.compile(cc);
                cc.addInsn(shortCircuit);
                break;
            case AND:
                shortCircuit = CompileContext.newLabel();
                if (l != null) l.compile(cc);
                if (r != null) r.compile(cc);
                cc.addInsn(shortCircuit);
                break;
            case NOT:
                cc.invertIfStmtLogic();
                if (l != null) l.compile(cc);
                if (r != null) r.compile(cc);
                cc.invertIfStmtLogic();
                break;
        }
        if (c != null) c.compile(cc);
        cc.setExitLabel(prevExitLabel);
    }
}
