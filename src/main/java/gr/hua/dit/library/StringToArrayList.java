package gr.hua.dit.library;

import gr.hua.dit.compiler.ast.FuncParams;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class StringToArrayList extends LangInternals {

    public static String name = "stringToArrayList";

    public String getDescriptor() {
        return "(Ljava/lang/String;)Ljava/util/ArrayList;";
    }

    private Type type = new FuncType(
        new FuncParams("s", DataType.String(), false),
        DataType.ProcType
    );

    // Index 0: number of characters to read from input
    // Index 1: string to store the read characters
    public void compile(CompileContext cc) {
        MethodNode pmn = cc.getCurrentMethodNode();
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, name, getDescriptor(), null, null);
        cc.setCurrentMethodNode(mn);

        // Create a new ArrayList to store the characters
        cc.addInsn(new TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"));
        cc.addInsn(new InsnNode(Opcodes.DUP));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V"));
        cc.addInsn(new VarInsnNode(Opcodes.ASTORE, 1));

//        // Get string size
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 0));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I"));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 2));

//        // Store the size of the string in register 3
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 2));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 3));

        // Loop through the string and add each character to the ArrayList
        LabelNode l1 = new LabelNode();
        LabelNode l2 = new LabelNode();
        cc.addInsn(l2);
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 2));
        cc.addInsn(new JumpInsnNode(Opcodes.IFLE, l1));
        // string.add(string.charAt(stringSize - index))
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1)); // Load string (ArrayList<byte>)
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 0)); // Load read string
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 3)); // Load size
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 2)); // Load index
        cc.addInsn(new InsnNode(Opcodes.ISUB));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C"));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;"));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z"));
        cc.irg.pop();

        // size--
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 2));
        cc.addInsn(new InsnNode(Opcodes.ICONST_1));
        cc.addInsn(new InsnNode(Opcodes.ISUB));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 2));

        cc.addInsn(new JumpInsnNode(Opcodes.GOTO, l2));
        cc.addInsn(l1);
//
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
        cc.addInsn(new InsnNode(Opcodes.ARETURN));
        mn.visitMaxs(32, 32); // specify max stack size and register size

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

