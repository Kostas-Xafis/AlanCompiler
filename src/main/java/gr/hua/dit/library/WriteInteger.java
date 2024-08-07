package gr.hua.dit.library;

import gr.hua.dit.compiler.ast.FuncParams;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.irgen.Descriptor;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.InputStream;

public class WriteInteger extends LangInternals {

    public static String name = "writeInteger";

    private Type type = new FuncType(
        new FuncParams("s", DataType.Int(), true),
        DataType.Proc()
    );


    public void compile(CompileContext cc) {
        String descriptor = Descriptor.build(type);
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, name, descriptor, null, null);
        mn.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", descriptor, false));
        mn.instructions.add(new InsnNode(Opcodes.RETURN));
        mn.maxLocals = 1;
        mn.maxStack = 2;

        cc.getClassNode().methods.add(mn);
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
