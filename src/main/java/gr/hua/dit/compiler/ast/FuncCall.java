package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.irgen.Descriptor;
import gr.hua.dit.compiler.symbol.SymbolEntry;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;
import gr.hua.dit.library.LangInternals;
import gr.hua.dit.library.Library;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.ArrayList;
import java.util.Optional;

public class FuncCall extends Expr<FuncType> {

    private final String functionName;
    private final ExprList args;

    private FuncType funcType;

    public FuncCall(String id, ExprList args) {
        super(null, id, args);
        this.setName("FuncCall");
        this.functionName = id;
        this.args = args;
        this.setInferredType(new FuncType(args, null));
    }

    public String toString() {
        return functionName + "(" + args + ")";
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        // Support for built-in functions is not implemented
        Optional<SymbolEntry> e = tbl.lookup(functionName);
        if (e.isPresent()) {
            funcType = (FuncType) e.get().getType();
            this.getInferredType().setResult(funcType.getResult());

            // Check if the function is called with the correct amount of arguments
            if (args == null && funcType.getArgTypes().isEmpty()) {
                return ;
            } else if (args == null) {
                throw SemanticException.IncorrectAmountOfFuncCallArgumentsException(tbl, functionName, 0);
            }
            args.sem(tbl);
            ArrayList<Type> funcDefArgTypes = funcType.getArgTypes();
            ArrayList<Type> funcCallArgTypes;
            funcCallArgTypes = args.getTypeList(null);

            // Check if the number of arguments match the number of arguments in the function definition
            if (funcDefArgTypes.size() != funcCallArgTypes.size()) {
                throw SemanticException.IncorrectAmountOfFuncCallArgumentsException(tbl, functionName, funcCallArgTypes.size());
            }

            // Check if the types of the arguments match the types of the function definition
            for (int i = 0; i < funcDefArgTypes.size(); i++) {
                if (!funcCallArgTypes.get(i).equals(funcDefArgTypes.get(i))) {
                    throw SemanticException.IncorrectFuncArgumentTypeException(tbl, functionName, i, funcCallArgTypes.get(i));
                }
            }
        } else {
            throw SemanticException.UndefinedFunctionException(functionName);
        }
    }

    public void compile(CompileContext cc) throws CompilerException {
        // Compile the function call
        if (args != null) {
            args.compile(cc);
        }
        LangInternals func = Library.getFunction(functionName);
        String className = cc.getClassNode().name;
        if (func != null) {
            System.out.println("Function " + functionName + " is a built-in function");
            String descriptor = Descriptor.build(func.getType());
            cc.addInsn(new MethodInsnNode(Opcodes.INVOKESTATIC, className, this.functionName, descriptor, false));
        } else {
            System.out.println("Function type is " + funcType);
            String descriptor = Descriptor.build(funcType);
            cc.addInsn(new MethodInsnNode(Opcodes.INVOKESTATIC, className, this.functionName, descriptor, false));
        }
    }
}
