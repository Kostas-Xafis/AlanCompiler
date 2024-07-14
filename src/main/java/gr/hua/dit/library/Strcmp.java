package gr.hua.dit.library;

import gr.hua.dit.compiler.ast.FuncParams;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class Strcmp extends LangInternals{

    public static String name = "strcmp";

    public String descriptor = "(Ljava/util/ArrayList;Ljava/util/ArrayList;)I";

    private Type type = new FuncType(
        new FuncParams("s1", DataType.String(), true,
            new FuncParams("s2", DataType.String(), true)),
        DataType.Int()
    );

    // Index 0: s1 string to compare
    // Index 1: s2 string to compare
    public void compile(CompileContext cc) {
        MethodNode pmn = cc.getCurrentMethodNode();
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, name, descriptor, null, null);
        cc.setCurrentMethodNode(mn);
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 0));
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
        // Check each string's length

        // Get the length of the first string
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 0));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "size", "()I", false));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 2));

        // Get the length of the second string
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "size", "()I", false));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 3));

        // Compare the lengths of the two strings
        LabelNode l1 = new LabelNode();
        LabelNode l2 = new LabelNode();
        LabelNode l4 = new LabelNode();
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 2));
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 3));
        cc.addInsn(new JumpInsnNode(Opcodes.IF_ICMPNE, l1));

        // If the lengths are equal check the characters one by one
        cc.addInsn(new InsnNode(Opcodes.ICONST_0));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 5));
        LabelNode l3 = new LabelNode();
        cc.addInsn(l3);
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 5));
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 2));
        cc.addInsn(new JumpInsnNode(Opcodes.IF_ICMPGE, l2));
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 0));
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 5));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;", false));
        cc.irg.typeCast("Char");
        cc.addInsn(new VarInsnNode(Opcodes.ASTORE, 2));
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 0));
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 5));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;", false));
        cc.irg.typeCast("Char");
        cc.addInsn(new VarInsnNode(Opcodes.ASTORE, 3));

        // Compare the characters
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 2));
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 3));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "equals", "(Ljava/lang/Object;)Z", false));
        cc.addInsn(new JumpInsnNode(Opcodes.IFNE, l1));
        cc.addInsn(new IincInsnNode(5, 1));
        cc.addInsn(new JumpInsnNode(Opcodes.GOTO, l3));

        cc.addInsn(l1);
        cc.addInsn(new InsnNode(Opcodes.ICONST_0));
        cc.addInsn(new JumpInsnNode(Opcodes.GOTO, l4));
        cc.addInsn(l2);
        cc.addInsn(new InsnNode(Opcodes.ICONST_1));
        cc.addInsn(l4);
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
