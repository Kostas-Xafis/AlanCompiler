package gr.hua.dit.compiler.errors;

import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;

public class SemanticException extends Exception {
    private String msg;

    static final String Error = BashColors.Red("\n\tError: ");

    public SemanticException(String msg) {
        super(msg);
    }

    public static SemanticException IncorrectAmountOfFuncCallArgumentsException(SymbolTable tbl, String funcName, Integer argNum) {
        FuncType ft = (FuncType) tbl.lookup(funcName).getType();
        return new SemanticException(Error +
            "\n\t\tFunction `" + BashColors.Cyan(funcName) + "` called with wrong number of arguments" +
            BashColors.Green("\n\t\tExpected: " + ft.getArgTypes().size()) +
            BashColors.Red("\n\t\tGot: " + argNum + "\n"));
    }

    public static SemanticException IncorrectFuncArgumentTypeException(SymbolTable tbl, String funcName, Integer argIndex, Type gotType) {
        FuncType ft = (FuncType) tbl.lookup(funcName).getType();
        Type argType = ft.getArgTypes().get(argIndex);
        return new SemanticException(Error +
            "\n\t\tFunction `" + BashColors.Cyan(funcName) + "` called with wrong type of argument" +
            BashColors.Green("\n\t\tExpected: " + argType) +
            BashColors.Red("\n\t\tGot: " + gotType + "\n"));
    }

    public static SemanticException NoReturnStatementException(String funcName) {
        return new SemanticException(Error +
            "\n\t\tFunction `" + BashColors.Cyan(funcName) + "` does not have a return statement\n");
    }

    public static SemanticException MainFunctionMustReturnProcException() {
        return new SemanticException(Error +
            "\n\t\tMain function must strictly return the proc type\n");
    }

    public static SemanticException UndefinedVariableException(String varName) {
        return new SemanticException(Error +
            "\n\t\tVariable `" + BashColors.Red(varName) + "` is not defined\n");
    }

    public static SemanticException UndefinedFunctionException(String funcName) {
        return new SemanticException(Error +
            "\n\t\tFunction `" + BashColors.Red(funcName) + "` is not defined\n");
    }

    public static SemanticException FunctionAlreadyDefinedException(String funcName) {
        return new SemanticException(Error +
            "\n\t\tFunction `" + BashColors.Red(funcName) + "` is already defined\n");
    }

    public static SemanticException VariableAlreadyDefinedException(String varName) {
        return new SemanticException(Error +
            "\n\t\tVariable `" + BashColors.Red(varName) + "` is already defined\n");
    }

    public static SemanticException AccessToNonArrayVariableException(String varName) {
        return new SemanticException(Error +
            "\n\t\tVariable `" + BashColors.Red(varName) + "` accessed as an array\n");
    }

    public static SemanticException ArrayIndexMustBeIntException(String arrayName) {
        return new SemanticException(Error +
            "\n\t\tArray `" + BashColors.Red(arrayName) + "` accessed with non-integer index\n");
    }

    public static SemanticException ArrayIndexOutOfBoundsException(SymbolTable tbl, String arrayName, Integer index) {
        Integer arraySize = ((DataType) tbl.lookup(arrayName).getType()).getArraySize();
        return new SemanticException(Error +
            "\n\t\tArray `" + BashColors.Red(arrayName) + "` accessed with out of bounds index" +
            BashColors.Green("\n\t\tArray Size: " + arraySize) +
            BashColors.Red("\n\t\tIndex: " + index + "\n"));
    }

    public static SemanticException ArrayIndexMustBePositiveException(String arrayName) {
        return new SemanticException(Error +
            "\n\t\tArray `" + BashColors.Red(arrayName) + "` accessed with negative index\n");
    }

}
