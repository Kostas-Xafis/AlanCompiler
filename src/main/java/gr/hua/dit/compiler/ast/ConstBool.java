package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.types.DataType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;

public class ConstBool extends Expr<DataType> {
    private final Boolean value;

    public ConstBool(Boolean value) {
        super(DataType.Bool(), value);
        this.value = value;
        this.setName("Bool_Const");
    }

    public String toString() {
        return "ConstBool(" + value.toString() + ")";
    }

    public void compile(CompileContext cc) throws CompilerException {
        cc.addInsn(new InsnNode(value ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
    }

    @Override
    public Boolean getValue() {
        return value;
    }
}
