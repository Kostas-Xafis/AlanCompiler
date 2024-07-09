package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.types.DataType;

public class ConstString extends Expr<DataType> {
    private final String value;

    public ConstString(String value) {
        super(DataType.String(), value);
        this.value = value;
        this.setName("String_Const");
    }

    public String toString() {
        return "ConstString(" + value + ")";
    }

    @Override
    public String getValue() {
        return value;
    }
}
