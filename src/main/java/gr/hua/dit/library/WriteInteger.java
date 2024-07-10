package gr.hua.dit.library;

import com.sun.org.apache.bcel.internal.generic.LoadInstruction;
import gr.hua.dit.compiler.ast.FuncParams;
import gr.hua.dit.compiler.irgen.Descriptor;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class WriteInteger extends LangInternals {

    private static final String owner = "gr/hua/dit/library";

    private String name = "writeInteger";

    private Type type = new FuncType(
        new FuncParams("s", DataType.Int(), true),
        DataType.Proc()
    );


    public void compile(ClassNode cn) {
        String descriptor = Descriptor.build(type);
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, name, "(I)V", null, null);
        mn.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", descriptor, false));
        mn.instructions.add(new InsnNode(Opcodes.RETURN));
        mn.maxLocals = 1;
        mn.maxStack = 2;

        cn.methods.add(mn);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
