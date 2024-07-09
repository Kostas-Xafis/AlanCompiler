package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.types.DataType;

public class ConstInt extends Expr<DataType> {
    private final Integer value;

    public ConstInt(Integer value) {
        super(DataType.Int(), value);
        this.value = value;
        this.setName("Int_Const");
    }

    public String toString() {
        return "ConstInt(" + value.toString() + ")";
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
