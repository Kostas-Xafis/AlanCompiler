package gr.hua.dit.compiler.irgen;

import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;

import java.util.ArrayList;

public class Descriptor {
    public static String build(Type type) {
        if (type.equals(DataType.IntType)) {
            return "I";
        } else if (type.equals(DataType.CharType)) {
            return "C";
        } else if (type.equals(DataType.StringType)) {
            return "Ljava/lang/String;";
        } else if (type.equals(DataType.ByteType)) {
            return "B";
        } else if (type.equals(DataType.BoolType)) {
            return "Z";
        } else if (type.equals(DataType.ProcType)) {
            return "V";
        } else if (type instanceof FuncType) {
            ArrayList<Type> args = ((FuncType) type).getArgTypes();
            Type result = ((FuncType) type).getResult();
            StringBuilder sb = new StringBuilder("(");
            for (Type arg : args) {
                sb.append(build(arg));
            }
            sb.append(")");
            sb.append(build(result));
            return sb.toString();
        } else {
            throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}
