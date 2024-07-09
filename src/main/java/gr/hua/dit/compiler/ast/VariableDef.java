package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;

public class VariableDef extends ASTNode<String> {

    private final DataType varType;
    private final String varName;

    public VariableDef(String id, DataType type) {
        super("VariableDef", id);
        this.varType = type;
        this.varName = id;
    }

    @Override
    public String toString() {
        return varName + " : " + varType;
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        // Check if variable is already defined
        if (tbl.lookup(varName, true) != null) {
            throw SemanticException.VariableAlreadyDefinedException(varName);
        }
        tbl.addEntry(varName, varType);
    }

    public String getVariableName() {
        return varName;
    }

    public DataType getVariableType() {
        return varType;
    }

}
