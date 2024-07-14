package gr.hua.dit.library;

import gr.hua.dit.compiler.ast.FuncParams;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class WriteString extends LangInternals {

    public static String name = "writeString";

    public String descriptor = "(Ljava/util/ArrayList;)V";

    private Type type = new FuncType(
        new FuncParams("s", DataType.String(), true),
        DataType.Proc()
    );

    public void compile(CompileContext cc) {
        MethodNode pmn = cc.getCurrentMethodNode();
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, name, "(Ljava/util/ArrayList;)V", null, null);
        cc.setCurrentMethodNode(mn);
        // Loop through the array list and print each character

        // Get the size of the array list
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 0));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "size", "()I"));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 1));

        // Store the size of the array list in register 2
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 1));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 2));

        LabelNode l1 = new LabelNode();
        LabelNode l2 = new LabelNode();
        cc.addInsn(l2);
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 1));
        cc.addInsn(new JumpInsnNode(Opcodes.IFLE, l1));
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 0));
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 2));
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 1));
        cc.addInsn(new InsnNode(Opcodes.ISUB));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;"));
        cc.addInsn(new VarInsnNode(Opcodes.ASTORE, 3));

        // Print the character
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 3));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/Object;)V"));

        // Decrement the size of the array list
        cc.addInsn(new IincInsnNode(1, -1));

        cc.addInsn(new JumpInsnNode(Opcodes.GOTO, l2));
        cc.addInsn(l1);

        cc.addInsn(new InsnNode(Opcodes.RETURN));
        mn.visitMaxs(4, 4);

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
