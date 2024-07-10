package gr.hua.dit.compiler.irgen;

import gr.hua.dit.compiler.symbol.SymbolTable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;

public class CompileContext {

    private SymbolTable symbolTable;
    public Stack stack;

    private ClassNode classNode;
    private MethodNode currentMethodNode;
    private Integer ScopeLevel = 0;
    private HashMap<String, Integer> counters = new HashMap<>();
    private HashMap<Integer, HashMap<String, Integer>> varLocation  = new HashMap<>();

    public CompileContext(ClassNode classNode) {
        this.symbolTable = symbolTable;
        this.classNode = classNode;
//        this.stack = new Stack();
        this.counters.put("int", 0);
        this.counters.put("char", 0);
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public ClassNode getClassNode() {
        return classNode;
    }

    public void setClassNode(ClassNode classNode) {
        this.classNode = classNode;
    }

    public MethodNode getCurrentMethodNode() {
        return currentMethodNode;
    }

    public void setCurrentMethodNode(MethodNode currentMethodNode) {
        this.currentMethodNode = currentMethodNode;
    }

    public void addInsn(AbstractInsnNode insn) {
        this.getCurrentMethodNode().instructions.add(insn);
    }

    public void addLocalInt(String name, Integer index) {
        System.out.println("Adding local int: " + name + " at index: " + index);
        varLocation.get(ScopeLevel).put(name, index);
    }

    public void addLocalInt(String name) {
        System.out.println("Adding local int: " + name);
        varLocation.get(ScopeLevel).put(name, counters.get("int"));
        counters.put("int", counters.get("int") + 1);
//        LabelNode start = new LabelNode();
//        LabelNode end = new LabelNode();
//        currentMethodNode.localVariables.add(new LocalVariableNode(name, "I", null, start, end, varLocation.get(name) + 1));
    }

    public void loadLocalInt(String name) {
        this.addInsn(new VarInsnNode(Opcodes.ILOAD, varLocation.get(ScopeLevel).get(name)));
    }

    public void storeLocalInt(String name) {
        this.addInsn(new VarInsnNode(Opcodes.ISTORE, varLocation.get(ScopeLevel).get(name)));
    }

    public void insertScope() {
        ScopeLevel++;
        varLocation.put(ScopeLevel, new HashMap<>());
    }

    public void removeScope() {
        varLocation.remove(ScopeLevel);
        ScopeLevel--;
    }
}
