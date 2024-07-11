package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

public class Ops extends Expr {

    public enum Operator {
        EQ("=="),
        NE("!="),
        LT("<"),
        GT(">"),
        LE("<="),
        GE(">=");

        private final String value;
        Operator(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

    public Operator op;
    private final Expr<?> l, r;

    public Ops(Operator o, Expr<?> l, Expr<?> r) {
        super(DataType.Bool(), o.toString(), l, r);
        this.setName("Ops");
        this.op = o;
        this.l = l;
        this.r = r;
    }

    public String toString() {
        return op.toString();
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        if (l == null || r == null) {
            throw new SemanticException("Left or right operand of Ops is missing");
        }
        l.sem(tbl);
        r.sem(tbl);
        l.typeCheckMatch(tbl, r);
    }

    public void compile(CompileContext cc) throws CompilerException {
        LabelNode exit = cc.getExitLabel();
        int logic = cc.getIfStmtLogic() ? 1 : 0;
        int code = 0;

        l.compile(cc);
        r.compile(cc);
        switch (op) {
            case EQ:
                code = (logic * Opcodes.IF_ICMPEQ) + (((logic + 1) % 2) * Opcodes.IF_ICMPNE);
                break;
            case NE:
                code = (logic * Opcodes.IF_ICMPNE) + (((logic + 1) % 2) * Opcodes.IF_ICMPEQ);
                break;
            case LT:
                code = (logic * Opcodes.IF_ICMPLT) + (((logic + 1) % 2) * Opcodes.IF_ICMPGE);
                break;
            case GT:
                code = (logic * Opcodes.IF_ICMPGT) + (((logic + 1) % 2) * Opcodes.IF_ICMPLE);
                break;
            case LE:
                code = (logic * Opcodes.IF_ICMPLE) + (((logic + 1) % 2) * Opcodes.IF_ICMPGT);
                break;
            case GE:
                code = (logic * Opcodes.IF_ICMPGE) + (((logic + 1) % 2) * Opcodes.IF_ICMPLT);
                break;
        }
        System.out.println("Ops: " + op + " " + code);
        cc.addInsn(new JumpInsnNode(code, exit));
    }
}
