package gr.hua.dit.compiler.irgen;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class IRGen {

    private final CompileContext cc;

    public IRGen(CompileContext cc) {
        this.cc = cc;
    }

    public void convertIntToInteger() {
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;"));
    }

    public void convertIntegerToInt() {
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I"));
    }

    void convertIntToByte() {
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;"));
    }

    public void convertIntegerToByte() {
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "byteValue", "()B"));
    }

    public void convertIntToChar() {
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;"));
    }

    public void typeCast(String type) {
        switch (type) {
            case "Integer":
                cc.addInsn(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Integer"));
                break;
            case "Char":
                cc.addInsn(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Character"));
                break;
            case "String":
                cc.addInsn(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/String"));
                break;
            case "Byte":
                cc.addInsn(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Byte"));
                break;
            case "ArrayList":
                cc.addInsn(new TypeInsnNode(Opcodes.CHECKCAST, "java/util/ArrayList"));
                break;
        }
    }
    public void pop() {
        cc.addInsn(new InsnNode(Opcodes.POP));
    }

    public void getArrayListSize(String name) {
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, cc.getClassNode().name, name, "Ljava/util/ArrayList;"));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "size", "()I"));
    }

    public void getArrayListLastItem(String name) {
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, cc.getClassNode().name, name, "Ljava/util/ArrayList;"));
        getArrayListSize(name);
        cc.addInsn(new InsnNode(Opcodes.ICONST_1));
        cc.addInsn(new InsnNode(Opcodes.ISUB));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;"));
    }

    public void shortGetByte(int offset) {
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 2));
        cc.addInsn(new VarInsnNode(Opcodes.BIPUSH, offset));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;"));
        typeCast("Byte");
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B"));
    }

    public void makeInt(int value) {
        cc.addInsn(new LdcInsnNode(value));
        cc.addInsn(new InsnNode(Opcodes.ICONST_0));
        cc.addInsn(new InsnNode(Opcodes.IADD));
    }

    public void printHeapEntry() {
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "size", "()I"));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 2));
        LabelNode l1 = new LabelNode();
        LabelNode l2 = new LabelNode();
        cc.addInsn(l2);
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 2));
        cc.addInsn(new JumpInsnNode(Opcodes.IFLE, l1));

        printInsn("Index = ", new VarInsnNode(Opcodes.ILOAD, 2));

        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 2));
        cc.addInsn(new InsnNode(Opcodes.ICONST_1));
        cc.addInsn(new InsnNode(Opcodes.ISUB));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;"));
        typeCast("Byte");
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "intValue", "()I"));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V"));

        // size--
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 2));
        cc.addInsn(new InsnNode(Opcodes.ICONST_1));
        cc.addInsn(new InsnNode(Opcodes.ISUB));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 2));
        cc.addInsn(new JumpInsnNode(Opcodes.GOTO, l2));
        cc.addInsn(l1);
    }

    public void printInsn(AbstractInsnNode insn) {
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        cc.addInsn(insn);
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V"));
    }

    public void printInsn(String msg, AbstractInsnNode insn) {
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        cc.addInsn(new LdcInsnNode(msg));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
        printInsn(insn);
    }

    public void print(String msg) {
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        cc.addInsn(new LdcInsnNode(msg));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
    }
}
