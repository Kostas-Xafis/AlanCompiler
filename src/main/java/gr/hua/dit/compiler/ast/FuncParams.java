package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.Type;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ArrayList;

public class FuncParams extends Expr {
    private final FuncParams next;
    private final String name;

    public FuncParams(String name, Type type, Boolean isRef, FuncParams next) {
        super(type, name, next);
        type.setRef(isRef);
        this.setName("FuncParams");
        this.next = next;
        this.name = name;
    }

    public FuncParams(String name, Type type, Boolean isRef) {
        this(name, type, isRef, null);
    }

    public ArrayList<Type> getParamTypes(ArrayList<Type> typeArr) {
        if (typeArr == null) {
            typeArr = new ArrayList<>();
        }
        typeArr.add(this.getInferredType().copy());
        if (next != null) {
            next.getParamTypes(typeArr);
        }
        return typeArr;
    }

    public ArrayList<Expr<?>> getParamList(ArrayList<Expr<?>> exprArr) {
        if (exprArr == null) {
            exprArr = new ArrayList<>();
        }
        exprArr.add(this);
        if (next != null) {
            next.getParamList(exprArr);
        }
        return exprArr;
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        tbl.addEntry(name, getInferredType());
        if (next != null) {
            next.sem(tbl);
        }
    }

    public void compile(CompileContext cc, Integer argInd) throws CompilerException {
        System.out.println("Compiling FuncParams: " + name + " at index: " + argInd);
        cc.addLocalInt(name, argInd);
        if (next != null) {
            next.compile(cc, argInd + 1);
        }
    }


    @Override
    public String toString() {
        return "FuncParams(" + name + ": " + getInferredType() + ", " + next + ")";
    }
}
