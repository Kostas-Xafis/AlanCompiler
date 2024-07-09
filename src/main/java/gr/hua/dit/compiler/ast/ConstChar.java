package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.types.DataType;

public class ConstChar extends Expr<DataType> {
    private final String value;

    public ConstChar(String value) {
        super(DataType.Byte(), value);
        this.value = value;
        this.setName("Char_Const");
    }

    public String toString() {
        return "ConstChar(" + value.toString() + ")";
    }

    @Override
    public String getValue() {
        return value;
    }
}
