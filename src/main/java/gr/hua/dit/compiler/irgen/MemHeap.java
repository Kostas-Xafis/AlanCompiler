package gr.hua.dit.compiler.irgen;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.types.DataType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.HashMap;

public class MemHeap {

    public class HeapEntry {
        public HashMap<String, DataType> types = new HashMap<>();
        public HashMap<String, Integer> integers = new HashMap<>();
        public HashMap<String, Integer> strings = new HashMap<>();
        public HashMap<String, Integer> chars = new HashMap<>();

        public String heapScope;
        private int heapSize = 64;
        private int heapByteOffset = 0;
        public ArrayList<Byte> heap = new ArrayList<>(heapSize);

        public HeapEntry(String name) {
            for (int i = 0; i < heapSize; i++) {
                heap.add((byte) 0);
            }
            heapScope = name;
            // Call in to createHeapEntry to add the heap entry to the compiled arraylist
            cc.addInsn(new LdcInsnNode(name));
            cc.addInsn(new MethodInsnNode(Opcodes.INVOKESTATIC, cc.getClassNode().name, "createHeapEntry", "(Ljava/lang/String;)V"));
        }

        // Put initializes the variable to the heap by adding the bytes to the heap
        public void init(String name, DataType type) {
            types.put(name, type);
            getEntry(null);
            cc.addInsn(new VarInsnNode(Opcodes.ASTORE, 1));
            if (type.equals(DataType.IntType)) {
                integers.put(name, heapByteOffset);
                // Load 2 bytes to initialize an integer to the heap
                cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
                generateEmptyByte();
                cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z"));

                cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
                generateEmptyByte();
                cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z"));
                // Load 2 byte initialize an integer to the heap
                heapByteOffset+=2;
            } else if (type.equals(DataType.StringType)) {
                strings.put(name, heapByteOffset);
                int stringSize = type.getArraySize();
                // Load 1 byte for each character in the string to initialize it

                cc.addInsn(new LdcInsnNode(stringSize));
                cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 3));

                LabelNode l1 = new LabelNode();
                LabelNode l2 = new LabelNode();
                cc.addInsn(l2);
                cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 3));
                cc.addInsn(new JumpInsnNode(Opcodes.IFLE, l1));
                cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
                generateEmptyByte();
                cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z"));
                irg.pop();
                cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 3));
                cc.addInsn(new InsnNode(Opcodes.ICONST_1));
                cc.addInsn(new InsnNode(Opcodes.ISUB));
                cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 3));
                cc.addInsn(new JumpInsnNode(Opcodes.GOTO, l2));
                cc.addInsn(l1);
                heapByteOffset += stringSize;
            } else if (type.equals(DataType.CharType) || type.equals(DataType.ByteType)) {
                chars.put(name, heapByteOffset);
                // Load 1 byte to initialize a char to the heap
                cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
                generateEmptyByte();
                cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z"));
                heapByteOffset++;
            }
        }

        public void getEntry(String name) {
            // Generate code to get the value from the heap inside the compiled arraylist
            cc.addInsn(new LdcInsnNode(name == null ? heapScope : name));
            cc.addInsn(new MethodInsnNode(Opcodes.INVOKESTATIC, cc.getClassNode().name, "peekHeapEntry", "(Ljava/lang/String;)Ljava/util/ArrayList;"));
        }

        public void getVariable(String varName) throws CompilerException {
            getEntry(null);
            cc.addInsn(new VarInsnNode(Opcodes.ASTORE, 1));
            // Check if the variable name exists in this scope
            // If it does, load the value from the heap
            if (types.containsKey(varName)) {
                DataType type = types.get(varName);
                if (type.equals(DataType.IntType)) {
                    int offset = integers.get(varName);
                    // Load 1st byte
                    cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
                    irg.makeInt(offset);
                    cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;"));
                    irg.typeCast("Byte");
                    cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "intValue", "()I"));
                    // Shift the first byte to the left by 8 bits
                    irg.makeInt(8);
                    cc.addInsn(new InsnNode(Opcodes.ISHL));
                    cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 2));

                    // Load 2nd byte
                    cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
                    irg.makeInt(offset + 1);
                    cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;"));
                    irg.typeCast("Byte");
                    cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "intValue", "()I"));

                    // Add the remaining last byte
                    cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 2));
                    cc.addInsn(new InsnNode(Opcodes.IADD));
                    cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 2));

                } else if (type.equals(DataType.StringType)) {
                    // Only a specific character will be changed at a time
                    // Offset goes at the 4th stack position
                    int offset = strings.get(varName);
                    cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
                    // Load character position
                    cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 4));
                    cc.addInsn(new InsnNode(Opcodes.ICONST_0));
                    cc.addInsn(new InsnNode(Opcodes.IADD));
                    // Load character
                    cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;"));

                } else if (type.equals(DataType.CharType)) {
                    int offset = chars.get(varName);
                    // Load 1st byte
                    cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
                    irg.makeInt(offset);
                    cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;"));
                    irg.typeCast("Byte");
                    cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "intValue", "()I"));
                }
            } else {
                throw new CompilerException("Variable " + varName + " not found in scope " + heapScope);
            }
        }

        // The variable must leave in the 4th stack position
        public void setVariable(String varName) throws CompilerException {
            getEntry(null);
            cc.addInsn(new VarInsnNode(Opcodes.ASTORE, 1));
            if (types.containsKey(varName)) {
                DataType type = types.get(varName);
                if (type.equals(DataType.IntType)) {
                    int offset = integers.get(varName);
                    irg.printInsn("Setting int variable " + varName + " of type int with value: ", new VarInsnNode(Opcodes.ILOAD, 4));
                    cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
                    irg.makeInt(offset);
                    cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 4));
                    cc.addInsn(new LdcInsnNode(8));
                    // Get the first byte
                    cc.addInsn(new InsnNode(Opcodes.ISHR));
                    irg.convertIntToByte();
                    cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "set", "(ILjava/lang/Object;)Ljava/lang/Object;"));

                    cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
                    irg.makeInt(offset + 1);
                    cc.addInsn(new LdcInsnNode(255));
                    cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 4));
                    cc.addInsn(new InsnNode(Opcodes.IAND));
                    // Get the first byte
                    irg.convertIntToByte();
                    cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "set", "(ILjava/lang/Object;)Ljava/lang/Object;"));

                } else if (type.equals(DataType.StringType)) {
                    // Only a specific character will be changed at a time
                    // Offset goes at the 5th stack position
                    int offset = strings.get(varName);
                    irg.print("Setting string variable " + varName);
                    cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
                    // Load character position
                    irg.makeInt(offset);
                    cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 5));
                    cc.addInsn(new InsnNode(Opcodes.IADD));
                    // Load character
                    cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 4));
                    irg.convertIntToByte();
                    cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "set", "(ILjava/lang/Object;)Ljava/lang/Object;"));
                } else if (type.equals(DataType.CharType)) {
                    int offset = chars.get(varName);
                    irg.printInsn("Setting int variable " + varName + " of type int with value: ", new VarInsnNode(Opcodes.ILOAD, 4));
                    cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
                    irg.makeInt(offset);
                    cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 4));
                    irg.convertIntToByte();
                    cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "set", "(ILjava/lang/Object;)Ljava/lang/Object;"));
                }
            } else {
                throw new CompilerException("Variable " + varName + " not found in scope " + heapScope);
            }
            // Print the resulting heap
            cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
            cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
            cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V"));

        }

        private void generateEmptyByte() {
            cc.addInsn(new InsnNode(Opcodes.ICONST_0));
            irg.convertIntToByte();
        }

        public void remove() {
            // Call in to removeHeapEntry to remove the heap entry from the compiled arraylist
            cc.addInsn(new LdcInsnNode(heapScope));
            cc.addInsn(new MethodInsnNode(Opcodes.INVOKESTATIC, cc.getClassNode().name, "removeHeapEntry", "(Ljava/lang/String;)V"));
        }
    }

    private final CompileContext cc;
    private final IRGen irg;
    public MemHeap(CompileContext cc) {
        this.cc = cc;
        this.irg = new IRGen(cc);
    }

    private final ArrayList<HeapEntry> heap = new ArrayList<>();

    private HeapEntry getCurrentHeap() {
        return heap.get(heap.size() - 1);
    }

    public void addHeapEntry(String name) {
        heap.add(new HeapEntry(name));
    }

    public void removeHeapEntry() {
        HeapEntry he = heap.remove(heap.size() - 1);
        he.remove();
    }

    public void init(String name, DataType type) {
        getCurrentHeap().init(name, type);
    }

    public void getVariable(String varName) throws CompilerException {
        getCurrentHeap().getVariable(varName);
    }

    public void setVariable(String varName) throws CompilerException {
        getCurrentHeap().setVariable(varName);
    }

    public void generateCompiledHeapStructure() throws CompilerException {
        // This should be called by main
        MethodNode pmn = cc.getCurrentMethodNode();


        // Create static hashmap field of type ArrayList<ArrayList<Byte>>
        ClassNode cn = cc.getClassNode();
        FieldNode fn = new FieldNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "theHeap", "Ljava/util/ArrayList;", null, null);
        cn.fields.add(fn);

        // Initialize the heap arraylist
        irg.print("Initializing the heap");
        InsnList il = new InsnList();
        il.add(new TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"));
        il.add(new InsnNode(Opcodes.DUP));
        il.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V"));
        il.add(new FieldInsnNode(Opcodes.PUTSTATIC, cn.name, "theHeap", "Ljava/util/ArrayList;"));
        pmn.instructions.add(il);

        // Create the static arraylist field `offsets` of type ArrayList<Integer>
        FieldNode fn2 = new FieldNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "offsets", "Ljava/util/ArrayList;", null, null);
        cn.fields.add(fn2);

        irg.print("Initializing the offsets arraylist");
        InsnList il2 = new InsnList();
        il2.add(new TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"));
        il2.add(new InsnNode(Opcodes.DUP));
        il2.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V"));
        il2.add(new FieldInsnNode(Opcodes.PUTSTATIC, cn.name, "offsets", "Ljava/util/ArrayList;"));
        pmn.instructions.add(il2);

        // Create the static arraylist field containing the call stack of type ArrayList<String>
        FieldNode fn3 = new FieldNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "callStack", "Ljava/util/ArrayList;", null, null);
        cn.fields.add(fn3);

        irg.print("Initializing the call stack arraylist");
        InsnList il3 = new InsnList();
        il3.add(new TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"));
        il3.add(new InsnNode(Opcodes.DUP));
        il3.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V"));
        il3.add(new FieldInsnNode(Opcodes.PUTSTATIC, cn.name, "callStack", "Ljava/util/ArrayList;"));
        pmn.instructions.add(il3);

        // Add the `main` function scope to the heap
        addHeapEntry("main");
        addHeapEntry("foo");
        init("bar", DataType.IntType);
        DataType str = DataType.String();
        str.setArray(10);
        init("baz", str);
        init("qux", DataType.CharType);

        cc.addInsn(new LdcInsnNode(22123));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 4));
        setVariable("bar");
        getVariable("bar");


        cc.addInsn(new LdcInsnNode('b'));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 4));
        setVariable("qux");
        getVariable("qux");

        cc.addInsn(new LdcInsnNode('b'));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 4));
        cc.addInsn(new LdcInsnNode(1));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 5));
        setVariable("baz");
        cc.addInsn(new LdcInsnNode(0));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 4));
        getVariable("baz");
//        getCurrentHeap().getEntry(null);
//        removeHeapEntry();
//        removeHeapEntry();

        generateCompiledHeapEntryPush();
        generateCompiledHeapEntryPop();
        generateCompiledPreviousHeapEntryPeek();
    }

    public void generateCompiledHeapEntryPush() {
        MethodNode pmn = cc.getCurrentMethodNode();
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "createHeapEntry", "(Ljava/lang/String;)V", null, null);
        cc.setCurrentMethodNode(mn);

        // Put compiled code that adds a new heap entry in the compiled arraylist
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 0));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));

        // Create the new ArrayList<Byte> for the heap entry
        cc.addInsn(new TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"));
        cc.addInsn(new InsnNode(Opcodes.DUP));
        cc.addInsn(new VarInsnNode(Opcodes.BIPUSH, 32));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "(I)V"));
        cc.addInsn(new VarInsnNode(Opcodes.ASTORE, 1));

        // Put the Arraylist in the compiled heap with .add
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, cc.getClassNode().name, "theHeap", "Ljava/util/ArrayList;"));
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z"));

        // Push the name of the heap entry to the offsets hashmap
        // like this:
        // Check if the function name is already the latest item in the callStack
        // if (callStack.size() > 0 && callStack.get(callStack.size() - 1).equals(name)) {
        //  Increment the offset
        //      curroffset = offsets.get(offsets.size() - 1);
        //      offsets.set(offsets.size() - 1, curroffset + 1);
        // } else {
        //      offsets.add(1);
        // }

        LabelNode l1 = new LabelNode();
        LabelNode l2 = new LabelNode();
        irg.getArrayListSize("callStack");
        cc.addInsn(new InsnNode(Opcodes.ICONST_0));
        cc.addInsn(new JumpInsnNode(Opcodes.IFLE, l1));
        irg.getArrayListLastItem("callStack");
        irg.typeCast("String");
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 0));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/String;)Z"));
        cc.addInsn(new JumpInsnNode(Opcodes.IFNE, l1));

        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, cc.getClassNode().name, "offsets", "Ljava/util/ArrayList;"));
        // offsets.size() - 1
        irg.getArrayListSize("offsets");
        cc.addInsn(new InsnNode(Opcodes.ICONST_1));
        cc.addInsn(new InsnNode(Opcodes.ISUB));

        // curroffset = offsets.get(offsets.size() - 1);
        irg.getArrayListLastItem("offsets");
        irg.typeCast("Integer");
        irg.convertIntegerToInt();
        cc.addInsn(new InsnNode(Opcodes.ICONST_1));
        cc.addInsn(new InsnNode(Opcodes.IADD));
        irg.convertIntToInteger();

        // offsets.set(offsets.size() - 1, curroffset + 1);
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "set", "(ILjava/lang/Object;)Ljava/lang/Object;"));
        irg.pop(); // Pop the returned Integer (previous value) off the stack, if you don't need it
        cc.addInsn(new JumpInsnNode(Opcodes.GOTO, l2));

        cc.addInsn(l1);
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, cc.getClassNode().name, "offsets", "Ljava/util/ArrayList;"));
        cc.addInsn(new InsnNode(Opcodes.ICONST_1));
        irg.convertIntToInteger();
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z"));
        irg.pop(); // Pop the returned Integer (previous value) off the stack, if you don't need it
        cc.addInsn(l2);

        // NOTE: Need to add it only if it's not already in the top of the call stack
        // Add to call stack
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, cc.getClassNode().name, "callStack", "Ljava/util/ArrayList;"));
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 0));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z"));

        // Print the new size offsets array size
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        irg.getArrayListSize("offsets");
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V"));

        cc.addInsn(new InsnNode(Opcodes.RETURN));
        mn.visitMaxs(16, 16);
        cc.getClassNode().methods.add(mn);

        cc.setCurrentMethodNode(pmn);
    }

    public void generateCompiledHeapEntryPop() {
        MethodNode pmn = cc.getCurrentMethodNode();
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "removeHeapEntry", "(Ljava/lang/String;)V", null, null);
        cc.setCurrentMethodNode(mn);
        // Put compiled code that removes the first heap entry in the compiled arraylist
        // like this:
        // theHeap.remove(theHeap.size() - 1);
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, cc.getClassNode().name, "theHeap", "Ljava/util/ArrayList;"));
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, cc.getClassNode().name, "theHeap", "Ljava/util/ArrayList;"));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "size", "()I"));
        cc.addInsn(new InsnNode(Opcodes.ICONST_1));
        cc.addInsn(new InsnNode(Opcodes.ISUB));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "remove", "(I)Ljava/lang/Object;"));


        // Remove the heap entry from the compiled arraylist

        // Convert Object to ArrayList and clear it
        irg.typeCast("ArrayList");
        cc.addInsn(new VarInsnNode(Opcodes.ASTORE, 1));
        cc.addInsn(new VarInsnNode(Opcodes.ALOAD, 1));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "clear", "()V"));

        // Print the previous offset value
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        irg.getArrayListLastItem("offsets");
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V"));

        // Make offset of the heap entry offset - 1
        // like this:
        // callStack.remove(callStack.size() - 1);
        // curroffset = offsets.get(offsets.size() - 1);
        // offsets.set(offsets.size() - 1, curroffset - 1);
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, cc.getClassNode().name, "callStack", "Ljava/util/ArrayList;"));
        irg.getArrayListSize("callStack");
        cc.addInsn(new InsnNode(Opcodes.ICONST_1));
        cc.addInsn(new InsnNode(Opcodes.ISUB));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "remove", "(I)Ljava/lang/Object;"));

        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, cc.getClassNode().name, "offsets", "Ljava/util/ArrayList;"));
        // offsets.size() - 1
        irg.getArrayListSize("offsets");
        cc.addInsn(new InsnNode(Opcodes.ICONST_1));
        cc.addInsn(new InsnNode(Opcodes.ISUB));

        // curroffset = offsets.get(offsets.size() - 1);
        irg.getArrayListLastItem("offsets");
        irg.typeCast("Integer");
        irg.convertIntegerToInt();
        cc.addInsn(new InsnNode(Opcodes.ICONST_1));
        cc.addInsn(new InsnNode(Opcodes.ISUB));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 2));
        irg.printInsn("Offset value: ", new VarInsnNode(Opcodes.ILOAD, 2));

        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 2));
        irg.convertIntToInteger();
        // offsets.set(offsets.size() - 1, curroffset - 1);
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "set", "(ILjava/lang/Object;)Ljava/lang/Object;"));
        irg.pop();

        cc.addInsn(new InsnNode(Opcodes.RETURN));
        mn.visitMaxs(3, 3);
        cc.getClassNode().methods.add(mn);

        cc.setCurrentMethodNode(pmn);
    }

    // Input a name of a heap entry and return the heap entry
    // NOTE: This returns only the first heap entry
    public void generateCompiledPreviousHeapEntryPeek() {
        MethodNode pmn = cc.getCurrentMethodNode();
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "peekHeapEntry", "(Ljava/lang/String;)Ljava/util/ArrayList;", null, null);
        cc.setCurrentMethodNode(mn);

        // Put compiled code that removes the first heap entry in the compiled arraylist
        // like this:
        // index = theHeap.size() - offset.get(offset.size() - 1) - 1
        // return theHeap.get(index);
        irg.getArrayListSize("theHeap");
        irg.getArrayListLastItem("offsets");
        irg.typeCast("Integer");
        irg.convertIntegerToInt();
        cc.addInsn(new InsnNode(Opcodes.ISUB));
        cc.addInsn(new InsnNode(Opcodes.ICONST_1));
        cc.addInsn(new InsnNode(Opcodes.ISUB));
        cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 1));
        cc.addInsn(new FieldInsnNode(Opcodes.GETSTATIC, cc.getClassNode().name, "theHeap", "Ljava/util/ArrayList;"));
        cc.addInsn(new VarInsnNode(Opcodes.ILOAD, 1));
        cc.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;"));
        irg.typeCast("ArrayList");

        // Return the array list of bytes
        cc.addInsn(new InsnNode(Opcodes.ARETURN));
        mn.visitMaxs(3, 3);
        cc.getClassNode().methods.add(mn);

        cc.setCurrentMethodNode(pmn);
    }

}
