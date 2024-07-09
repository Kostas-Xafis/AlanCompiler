package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.symbol.SymbolEntry;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;

import java.util.ArrayList;

public class FuncCall extends Expr<FuncType> {

    private final String functionName;
    private final ExprList args;

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
        SymbolEntry e = tbl.lookup(functionName);
        if (e != null) {
            FuncType funcType = (FuncType) e.getType();
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
}