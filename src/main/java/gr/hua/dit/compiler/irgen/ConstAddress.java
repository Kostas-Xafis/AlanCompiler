package gr.hua.dit.compiler.irgen;

public class ConstAddress extends Address {

    private Integer constant;
    public ConstAddress(Integer constant) {
        this.constant = constant;
    }

    @Override
    public String toString() {
        return constant.toString();
    }
}
