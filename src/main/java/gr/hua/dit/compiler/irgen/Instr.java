package gr.hua.dit.compiler.irgen;

public class Instr {
    private final String op;
    private final Address arg1;
    private final Address arg2;
    private final Address result;

    public Instr(String op, Address arg1, Address arg2, Address result) {
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = result;
    }

    public Instr(String op, Address arg1, Address result) {
        this(op, arg1, null, result);
    }

    public Instr(String op, Address result) {
        this(op, null, null, result);
    }

    public String toString() {
        return op + " " + arg1 + " " + arg2 + " " + result;
    }
}
