package gr.hua.dit.compiler.errors;

import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;

public class SemanticException extends Exception {
    private String msg;

    public SemanticException(String msg) {
        super(msg);
    }

    public static SemanticException IncorrectAmountOfFuncCallArgumentsException(SymbolTable tbl, String funcName, Integer argNum) {
        FuncType ft = (FuncType) tbl.lookup(funcName).getType();
        return new SemanticException("\n\tError: " +
            "\n\t\tFunction `" + funcName + "` called with wrong number of arguments" +
            "\n\t\tExpected: " + ft.getArgTypes().size() +
            "\n\t\tGot: " + argNum + "\n");
    }

    public static SemanticException IncorrectArgumentTypeException(SymbolTable tbl, String funcName, Integer argIndex, Type gotType) {
        FuncType ft = (FuncType) tbl.lookup(funcName).getType();
        Type argType = ft.getArgTypes().get(argIndex);
        return new SemanticException("\n\tError: " +
            "\n\t\tFunction `" + funcName + "` called with wrong type of argument" +
            "\n\t\tExpected: " + argType +
            "\n\t\tGot: " + gotType + "\n");
    }

    public static SemanticException UndefinedVariableException(String varName) {
        return new SemanticException("\n\tError: " +
            "\n\t\tVariable `" + varName + "` is not defined\n");
    }

    public static SemanticException UndefinedFunctionException(String funcName) {
        return new SemanticException("\n\tError: " +
            "\n\t\tFunction `" + funcName + "` is not defined\n");
    }

    public static SemanticException AccessToNonArrayVariableException(String varName) {
        return new SemanticException("\n\tError: " +
            "\n\t\tVariable `" + varName + "` accessed as an array\n");
    }

    public static SemanticException ArrayIndexMustBeIntException(String arrayName) {
        return new SemanticException("\n\tError: " +
            "\n\t\tArray `" + arrayName + "` accessed with non-integer index\n");
    }

    public static SemanticException ArrayIndexOutOfBoundsException(SymbolTable tbl, String arrayName, Integer index) {
        Integer arraySize = ((DataType) tbl.lookup(arrayName).getType()).getArraySize();
        return new SemanticException("\n\tError: " +
            "\n\t\tArray `" + arrayName + "` accessed with out of bounds index" +
            "\n\t\tArray size: " + arraySize +
            "\n\t\tIndex: " + index + "\n");
    }

    public static SemanticException ArrayIndexMustBePositiveException(String arrayName) {
        return new SemanticException("\n\tError: " +
            "\n\t\tArray `" + arrayName + "` accessed with negative index\n");
    }

}
