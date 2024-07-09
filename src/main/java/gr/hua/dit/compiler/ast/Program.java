package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;

public class Program extends ASTNode {

    private final FuncDef main;

    public Program(FuncDef main) {
        super("Program", null, main);
        this.main = main;
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        if(!main.getFuncDefType().getResult().equals(DataType.ProcType)) {
            throw SemanticException.MainFunctionMustReturnProcException();
        };
        main.sem(tbl);
    }
}
