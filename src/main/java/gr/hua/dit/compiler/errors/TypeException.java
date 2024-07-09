package gr.hua.dit.compiler.errors;

import gr.hua.dit.compiler.types.Type;

public class TypeException extends SemanticException {

    public TypeException(String msg) {
        super(msg);
    }

    public TypeException(Type t1, Type t2) {
        super("Error: \n\tType mismatch: " +
            "\n\t\tExpected: " + t1 + ", " +
            "\n\t\tGot: " + t2);
    }

    public TypeException(String location, Type t1, Type t2) {
        super("Error at " + location +  " node: \n\tType mismatch: " +
            "\n\t\tExpected: " + t1 + ", " +
            "\n\t\tGot: " + t2);
    }
}
