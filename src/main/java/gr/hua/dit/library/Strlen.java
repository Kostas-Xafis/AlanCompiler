package gr.hua.dit.library;

import gr.hua.dit.compiler.ast.FuncParams;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class Strlen extends LangInternals {

    public static String name = "strlen";

    public String descriptor = "(Ljava/util/ArrayList;)I";

    private Type type = new FuncType(
        new FuncParams("s", DataType.String(), true),
        DataType.IntType
    );



    // Index 0: number of characters to read from input
    // Index 1: string to store the read characters
    public void compile(CompileContext cc) {
        MethodNode pmn = cc.getCurrentMethodNode();
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, name, descriptor, null, null);
        cc.setCurrentMethodNode(mn);
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 0));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "size", "()I", false));
        cc.addInsn(new InsnNode(Opcodes.IRETURN));
        mn.visitMaxs(1, 2); // specify max stack size and register size

        cc.getClassNode().methods.add(mn);
        cc.setCurrentMethodNode(pmn);
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
