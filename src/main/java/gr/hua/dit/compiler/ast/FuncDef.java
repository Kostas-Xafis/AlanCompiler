package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.irgen.Descriptor;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FuncDef extends ASTNode<String> {

    private final String functionName;
    private final FuncParams args;
    private final FuncType funcType;
    private final LocalDef localDef;
    private final Statement compoundStmt;

    private String descriptor;

    public FuncDef(String id, Type returnType, Statement CompoundStmt, FuncParams args, LocalDef localDef) {
        super("FuncDef", id);
        ArrayList<ASTNode> children = (ArrayList<ASTNode>) new ArrayList<>(Arrays.asList(args,  localDef, CompoundStmt)).stream().filter(x -> x != null).collect(Collectors.toList());
        this.setChildren(children);
        this.functionName = id;
        this.args = args;
        this.funcType = new FuncType(args, returnType);
        this.localDef = localDef;
        this.compoundStmt = CompoundStmt;
    }

    @Override
    public String toString() {
        return functionName + "(" + args + ")" + " : " + funcType.getResult();
    }

    public String getFunctionName() {
        return functionName;
    }

    public FuncType getFuncDefType() {
        return funcType;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        // Check if function is already defined
        if (tbl.lookup(functionName).isPresent()) {
            throw SemanticException.FunctionAlreadyDefinedException(functionName);
        }
        tbl.addEntry(functionName, funcType);
        tbl.openScope();
        if (args != null) args.sem(tbl);
        if (localDef != null) localDef.sem(tbl);
        if (compoundStmt != null) compoundStmt.sem(tbl);

        if (!funcType.getResult().equals(DataType.ProcType)) {
            boolean hasReturn = !this.getNodes("ReturnStmt").isEmpty();
            if (!hasReturn) {
                throw SemanticException.NoReturnStatementException(functionName);
            }
        }
        descriptor = Descriptor.build(funcType);
        tbl.closeScope();
    }

    public void compile(CompileContext cc) throws CompilerException {
        System.out.println("Compiling function: " + functionName + " with type: " + funcType);
        MethodNode prevMn = cc.getCurrentMethodNode();
        LabelNode earlyExit = CompileContext.newLabel();
        LabelNode prevEarlyExit = cc.getEarlyExitLabel();
        cc.setEarlyExitLabel(earlyExit);
        if (!functionName.equals("main")) {
            MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, functionName, Descriptor.build(funcType), null, null);
            mn.visitMaxs(64, 64);
            cc.getClassNode().methods.add(mn);
            cc.setCurrentMethodNode(mn);
        }
        cc.openScope();
        if (args != null) {
            args.compile(cc);
        }
        if (localDef != null) {
            localDef.compile(cc);
        }
        compoundStmt.compile(cc);
        if (!funcType.getResult().equals(DataType.ProcType)) {
            cc.addInsn(earlyExit);
        }
        if (!functionName.equals("main")) {
            if(funcType.getResult().equals(DataType.IntType)) {
                cc.getCurrentMethodNode().visitInsn(Opcodes.IRETURN);
            } else if (!funcType.getResult().equals(DataType.ProcType)) {
                cc.getCurrentMethodNode().visitInsn(Opcodes.ARETURN);
            } else {
                cc.getCurrentMethodNode().visitInsn(Opcodes.RETURN);
            }

        }

        cc.setEarlyExitLabel(prevEarlyExit);

        cc.setCurrentMethodNode(prevMn);
        cc.closeScope();
    }
}
