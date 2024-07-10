package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;

public class Mops extends Expr<DataType> {

    public enum Operator {
        ADD("+"),
        SUB("-"),
        MUL("*"),
        DIV("/"),
        MOD("%"),
        PLUS_SIGN("+"),
        MINUS_SIGN("-");
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
    public Mops(Operator o, Expr<?> l, Expr<?> r) {
        super(DataType.Int(), o.toString(), l, r);
        this.setName("Mops");
        this.op = o;
        this.l = l;
        this.r = r;
    }

    public Mops(Operator o, Expr<?> l) {
        this(o, l, null);
    }

    public String toString() {
        return "Mops(" + l + op + (r != null ? r.toString() : "") + ")";
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        if (l != null) {
            l.sem(tbl);
            l.typeCheck(tbl, DataType.IntType);
        }
        if (r != null) {
            r.sem(tbl);
            r.typeCheck(tbl, DataType.IntType);
        }
    }

    public void compile(CompileContext cc) throws CompilerException {
        int opcode;
        switch (op) {
            case ADD:
                opcode = Opcodes.IADD;
                break;
            case SUB:
                opcode = Opcodes.ISUB;
                break;
            case MUL:
                opcode = Opcodes.IMUL;
                break;
            case DIV:
                opcode = Opcodes.IDIV;
                break;
            case MOD:
                opcode = Opcodes.IREM;
                break;
            case PLUS_SIGN:
                opcode = Opcodes.NOP;
                break;
            case MINUS_SIGN:
                opcode = Opcodes.INEG;
                break;
            default:
                throw new CompilerException("Unknown operator: " + op);
        }
        if (r != null) {
            l.compile(cc);
            r.compile(cc);
            cc.addInsn(new InsnNode(opcode));
        } else {
            l.compile(cc);
        }
    }
}
