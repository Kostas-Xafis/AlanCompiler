package gr.hua.dit.library;

import gr.hua.dit.compiler.ast.FuncParams;
import gr.hua.dit.compiler.irgen.Descriptor;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class WriteString extends LangInternals {

    private String name = "writeString";

    private Type type = new FuncType(
        new FuncParams("s", DataType.String(), true),
        DataType.Proc()
    );

    public void compile(ClassNode cn) {
        String descriptor = Descriptor.build(type);
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, name, descriptor, null, null);
        mn.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", descriptor, false));
        mn.instructions.add(new InsnNode(Opcodes.RETURN));
        mn.maxLocals = 1;
        mn.maxStack = 0;

        cn.methods.add(mn);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

}
