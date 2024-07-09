package gr.hua.dit.compiler.irgen;

public class LabelAddress extends Address {
    private final String label;

    public LabelAddress(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
