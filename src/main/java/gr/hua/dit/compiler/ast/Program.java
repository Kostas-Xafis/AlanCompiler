package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.symbol.SymbolTable;

public class Program extends ASTNode {

    private final FuncDef main;

    public Program(FuncDef main) {
        super("Program", null, main);
        this.main = main;
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        main.sem(tbl);
    }
}
