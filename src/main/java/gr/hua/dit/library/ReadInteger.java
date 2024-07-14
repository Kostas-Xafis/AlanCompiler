package gr.hua.dit.library;

import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.irgen.Descriptor;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ReadInteger extends LangInternals {

    public static String name = "readInteger";

    private Type type = new FuncType(DataType.Int());


    // Compilation of read integer function
    public void compile(CompileContext cc) {
        String descriptor = Descriptor.build(type);
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, name, descriptor, null, null);

        mn.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/Scanner"));
        mn.instructions.add(new InsnNode(Opcodes.DUP));
        mn.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;"));
        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false));
        mn.instructions.add(new VarInsnNode(Opcodes.ASTORE, 1)); // Store Scanner in local variable 1

        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1)); // Load Scanner from local variable 1
        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false));
        mn.instructions.add(new VarInsnNode(Opcodes.ISTORE, 2));

        // Load the read integer
        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));

        // Return from bar method
        mn.instructions.add(new InsnNode(Opcodes.IRETURN));
        mn.visitMaxs(3, 3); // specify max stack size and local variables

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
