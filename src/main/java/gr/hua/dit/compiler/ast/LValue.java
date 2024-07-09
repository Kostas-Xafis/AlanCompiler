package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.errors.TypeException;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;

public class LValue extends Expr<DataType> {
    private final String name;
    private Expr<?> expr;


    public LValue(String name, Expr<?> expr) {
        super(null, name, expr);
        this.setName("LValue");
        this.name = name;
        this.expr = expr;
    }

    public LValue(String name) {
        super(null, name);
        this.name = name;
    }

    public String toString() {
        return "Lvalue(" + name + (expr != null ? "[" + expr + "]" : "") + ")";
    }

    public String getName() {
        return name;
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        // Check if variable is already defined
        DataType type = (DataType) tbl.lookup(getName(), true).getType();
        this.setInferredType(type);

        // Check if the variable is an array and if it is accessed correctly
        if (expr != null) {
            if (!type.isArray()) {
                throw SemanticException.AccessToNonArrayVariableException(getName());
            }
            expr.sem(tbl);
            this.getInferredType().setAccessed(true);
            if(!expr.getInferredType().equals(DataType.IntType)) {
                throw new SemanticException("Array index must be of type 'int'");
            }
        }
//        System.out.println("LValue: " + getName() + ":" + type + " " + expr);
    }
}
