package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.types.DataType;

public class ConstBool extends Expr<DataType> {
    private final Boolean value;

    public ConstBool(Boolean value) {
        super(DataType.Bool(), value);
        this.value = value;
        this.setName("Bool_Const");
    }

    public String toString() {
        return "ConstBool(" + value.toString() + ")";
    }

    @Override
    public Boolean getValue() {
        return value;
    }
}
