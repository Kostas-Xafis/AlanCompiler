package gr.hua.dit.compiler.errors;

import gr.hua.dit.compiler.types.Type;

public class TypeException extends SemanticException {
    static final String Error = BashColors.Red("\n\tError: ");

    public TypeException(String msg) {
        super(msg);
    }

    public TypeException(Type t1, Type t2) {
        super(Error + "\n\tType mismatch: " +
            BashColors.Green("\n\t\tExpected: " + t1) +
            BashColors.Red("\n\t\tGot: " + t2));
    }

    public TypeException(String location, Type t1, Type t2) {
        super(Error + "\n\tType mismatch in " + BashColors.Cyan(location) + ": " +
            BashColors.Green("\n\t\tExpected: " + t1) +
            BashColors.Red("\n\t\tGot: " + t2));
    }
}
