package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.types.DataType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class ConstString extends Expr<DataType> {
    private final String value;

    public ConstString(String value) {
        super(DataType.String(), value);
        this.value = value.substring(1, value.length() - 1)
            .replace("\\n", "\n")
            .replace("\\t", "\t")
            .replace("\\r", "\t")
            .replace("\\0", "\0")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\");
        System.out.println("ConstString: " + this.value + " length: " + this.value.length());
        this.setName("String_Const");
    }

    public String toString() {
        return "ConstString(" + value + ")";
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void compile(CompileContext cc) throws CompilerException {
        cc.addInsn(new LdcInsnNode(value));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKESTATIC, "MiniBasic", "stringToArrayList", "(Ljava/lang/String;)Ljava/util/ArrayList;", false));
    }
}
