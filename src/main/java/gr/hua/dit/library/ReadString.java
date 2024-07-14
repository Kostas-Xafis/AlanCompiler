package gr.hua.dit.library;

import gr.hua.dit.compiler.ast.FuncParams;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.irgen.Descriptor;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Scanner;

public class ReadString extends LangInternals {

    public static String name = "readString";

    public String getDescriptor() {
        return "(ILjava/util/ArrayList;)V";
    }

    private Type type = new FuncType(
        new FuncParams("n", DataType.Int(), false,
            new FuncParams("s", DataType.String(), true)),
        DataType.ProcType
    );

    // Index 0: number of characters to read from input
    // Index 1: string to store the read characters
    public void compile(CompileContext cc) {
        MethodNode pmn = cc.getCurrentMethodNode();
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, name, "(ILjava/util/ArrayList;)V", null, null);
        cc.setCurrentMethodNode(mn);
        cc.addInsn(new TypeInsnNode(Opcodes.NEW, "java/util/Scanner"));
        cc.addInsn(new InsnNode(Opcodes.DUP));
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;"));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V"));
        cc.addInsn(new VarInsnNode(Opcodes.ASTORE, 2)); // Store Scanner in register  2

        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 2)); // Load Scanner from register  2
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "next", "()Ljava/lang/String;"));
        cc.addInsn(new VarInsnNode(Opcodes.ASTORE, 2)); // Store the new string in register  2

        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 2));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I"));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 3)); // Store the length of the string in register  3

        // If the number of characters read is more than the number of characters to read
        // substring the string to the number of characters to read
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 3));
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 0));
        LabelNode l1 = new LabelNode();
        cc.irg.print("String is longer than expected");
        cc.addInsn(new JumpInsnNode(Opcodes.IF_ICMPLE, l1));
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 2));
        cc.addInsn(new InsnNode(Opcodes.ICONST_0));
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 0));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "substring", "(II)Ljava/lang/String;"));
        cc.addInsn(new VarInsnNode(Opcodes.ASTORE, 2));

        // Set string length back to the number of characters to read
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 0));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 3));

        cc.addInsn(l1);

        // Clear the array list of any previous characters
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "clear", "()V"));

        // Store the size of the string in register 4
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 3));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 4));


        LabelNode l2 = new LabelNode();
        LabelNode l3 = new LabelNode();
        cc.addInsn(l3);
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 3));
        cc.addInsn(new JumpInsnNode(Opcodes.IFLE, l2));
        // string.add(string.charAt(stringSize - index))
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1)); // Load string (ArrayList<byte>)
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 2)); // Load read string
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 4)); // Load size
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 3)); // Load index
        cc.addInsn(new InsnNode(Opcodes.ISUB));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C"));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;"));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z"));
        cc.irg.pop();

        // size--
        cc.addInsn(new IincInsnNode(3, -1));

        cc.addInsn(new JumpInsnNode(Opcodes.GOTO, l3));
        cc.addInsn(l2);

        cc.addInsn(new InsnNode(Opcodes.RETURN));
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
