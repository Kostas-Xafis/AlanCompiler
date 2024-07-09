package gr.hua.dit.compiler.irgen;

public class VarAddress extends Address {
    private String name;

    public VarAddress(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
