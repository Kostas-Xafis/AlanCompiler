package gr.hua.dit.compiler.irgen;

import gr.hua.dit.compiler.ast.ASTNode;
import gr.hua.dit.compiler.ast.Expr;
import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.symbol.SymbolEntry;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Optional;

public class CompileContext {

    private SymbolTable symbolTable;
    private MemHeap heap;
    public IRGen irg;

    private ClassNode classNode;
    private MethodNode currentMethodNode;
    private Integer ScopeLevel = 0;
    private HashMap<Integer, Integer> counters = new HashMap<>();
    private HashMap<Integer, HashMap<String, Integer>> varLocation  = new HashMap<>();

    public CompileContext(ClassNode classNode) {
        this.classNode = classNode;
        this.irg = new IRGen(this);
    }

    public void constructHeap() throws CompilerException {
//        this.heap = new MemHeap(this);
//        heap.generateCompiledHeapStructure();
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

    public void addLocal(String name, DataType type) {
        System.out.println("Adding local: " + name + " : " + type);
        int localIndex = counters.get(ScopeLevel);
        if (type.equals(DataType.StringType)) {
            // if it's not a reference type, we need to create a new ArrayList
            if (!type.isRef()) {
                this.addInsn(new TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"));
                this.addInsn(new InsnNode(Opcodes.DUP));
                this.addInsn(new LdcInsnNode(type.getArraySize()));
                this.addInsn(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "(I)V"));
                this.addInsn(new VarInsnNode(Opcodes.ASTORE, localIndex));
            }
        }
        varLocation.get(ScopeLevel).put(name, localIndex);
        counters.put(ScopeLevel, counters.get(ScopeLevel) + 1);
        symbolTable.addEntry(name, type);
    }

    public void loadLocal(String name) throws CompilerException {
        loadLocal(name, null, null);
    }

    public void loadLocal(String name, DataType dt, ASTNode accessorNode) throws CompilerException {
        // Find the variable in the current scope
        Integer index = varLocation.get(ScopeLevel).get(name);
        Optional<SymbolEntry> s = symbolTable.lookup(name);
        if (s.isPresent()) {
            DataType type = dt != null ? dt : (DataType) s.get().getType();
            if (type.equals(DataType.IntType)) {
                this.addInsn(new VarInsnNode(Opcodes.ILOAD, index));
            } else if (type.equals(DataType.CharType)) {
                if (!type.isAccessed()) {
                    this.addInsn(new VarInsnNode(Opcodes.ILOAD, index));
                } else {
                    this.addInsn(new VarInsnNode(Opcodes.ALOAD, index));
                    accessorNode.compile(this);
                    this.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;"));
                }
            } else if (type.equals(DataType.StringType)) {
                this.addInsn(new VarInsnNode(Opcodes.ALOAD, index));
            } else {
                throw new RuntimeException("Unsupported variable type: " + type);
            }
        } else {
            throw new RuntimeException("Variable not found: " + name);
        }
    }

    public void storeLocal(String name) throws CompilerException {
        storeLocal(name, null, null, null);
    }

    public void storeLocal(String name,  DataType dt) throws CompilerException {
        storeLocal(name, dt, null, null);
    }

    public void storeLocal(String name, DataType dt, Expr insertedValue, Expr accessorNode) throws CompilerException {
        // Find the variable in the current scope
        System.out.println("" + varLocation.get(ScopeLevel));
        Integer index = varLocation.get(ScopeLevel).get(name);
        Optional<SymbolEntry> s = symbolTable.lookup(name);

        if (s.isPresent()) {
            DataType type = dt != null ? dt : (DataType) s.get().getType();
            if (type.equals(DataType.IntType)) {
                this.addInsn(new VarInsnNode(Opcodes.ISTORE, index));
            } else if (type.equals(DataType.CharType)) {
                if (type.isAccessed()) {
                    this.addInsn(new VarInsnNode(Opcodes.ALOAD, index));
                    accessorNode.compile(this);
                    insertedValue.compile(this);
                    if (((DataType) insertedValue.getInferredType()).isAccessed()) {
                        this.irg.typeCast("Char");
                    } else {
                        this.irg.convertIntToChar();
                    }
                    this.addInsn(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "set", "(ILjava/lang/Object;)Ljava/lang/Object;"));
                    this.irg.pop();
                } else {
                    this.addInsn(new VarInsnNode(Opcodes.ISTORE, index));
                }
            } else if (type.equals(DataType.StringType)) {
                this.addInsn(new VarInsnNode(Opcodes.ASTORE, index));
            } else {
                throw new RuntimeException("Unsupported variable type: " + type);
            }
        } else {
            throw new RuntimeException("Variable not found: " + name);
        }
    }

    public void openScope() {
        ScopeLevel++;
        varLocation.put(ScopeLevel, new HashMap<>());
        symbolTable.openScope();
        counters.put(ScopeLevel, 0);
    }

    public void closeScope() {
        varLocation.remove(ScopeLevel);
        symbolTable.closeScope();
        counters.remove(ScopeLevel);
        ScopeLevel--;
    }

    public static LabelNode newLabel() {
        return new LabelNode();
    }

    private LabelNode exitLabel;
    public void setExitLabel(LabelNode exitLabel) {
        this.exitLabel = exitLabel;
    }
    public LabelNode getExitLabel() {
        return exitLabel;
    }


    private LabelNode earlyExitLabel;
    public LabelNode getEarlyExitLabel() {
        return earlyExitLabel;
    }
    public void setEarlyExitLabel(LabelNode earlyExitLabel) {
        this.earlyExitLabel = earlyExitLabel;
    }

    private boolean MathOpsSign = true;
    public void invertMathOpsSign() {
        MathOpsSign = !MathOpsSign;
    }
    public boolean getMathOpsSign() {
        return MathOpsSign;
    }


    // Inverted or not logic for simplifying the code
    private boolean ifstmtLogic = false;
    public void invertIfStmtLogic() {
        ifstmtLogic = !ifstmtLogic;
    }

    public boolean getIfStmtLogic() {
        return ifstmtLogic;
    }

}
