package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.types.DataType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.VarInsnNode;

public class ConstInt extends Expr<DataType> {
    private final Integer value;

    public ConstInt(Integer value) {
        super(DataType.Int(), value);
        this.value = value;
        this.setName("Int_Const");
    }

    public String toString() {
        return "ConstInt(" + value.toString() + ")";
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void compile(CompileContext cc) throws CompilerException {
        if (this.value < 128 && this.value >= -128) {
            cc.addInsn(new VarInsnNode(Opcodes.BIPUSH, this.value));
        } else {
            cc.addInsn(new VarInsnNode(Opcodes.SIPUSH, this.value));
        }
    }
}
