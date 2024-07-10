package gr.hua.dit.compiler.irgen;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.HashMap;

public class Stack {

    private ClassNode cn;
    private HashMap<String, Integer> counters = new HashMap<>();
    public Stack(CompileContext cc) {
//        this.cn = cc.getClassNode();

//        cn.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, "intVars", "[I", null, null));
//        cn.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, "stringVars", "[Ljava/lang/String;", null, null));
//        cn.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, "charVars", "[C", null, null));
//
//        counters.put("int", 0);
//        counters.put("string", 0);
//        counters.put("char", 0);
    }

    public void pushInt(int value) {

    }





}
