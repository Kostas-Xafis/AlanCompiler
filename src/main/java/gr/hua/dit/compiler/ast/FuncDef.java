package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FuncDef extends ASTNode<String> {

    private final String functionName;
    private final FuncParams args;
    private final FuncType funcType;
    private final LocalDef localDef;
    private final Statement compoundStmt;

    public FuncDef(String id, Type returnType, Statement CompoundStmt, FuncParams args, LocalDef localDef) {
        super("FuncDef", id);
        ArrayList<ASTNode> children = (ArrayList<ASTNode>) new ArrayList<>(Arrays.asList(args,  localDef, CompoundStmt)).stream().filter(x -> x != null).collect(Collectors.toList());
        this.setChildren(children);
        this.functionName = id;
        this.args = args;
        this.funcType = new FuncType(args, returnType);
        this.localDef = localDef;
        this.compoundStmt = CompoundStmt;
    }

    @Override
    public String toString() {
        return functionName + "(" + args + ")" + " : " + funcType.getResult();
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        // Check if function is already defined
        if (tbl.lookup(functionName) != null) {
            throw new SemanticException("Function " + functionName + " already defined");
        }
        tbl.addEntry(functionName, funcType);
        tbl.openScope();
        if (args != null) args.sem(tbl);
        if (localDef != null) localDef.sem(tbl);
        if (compoundStmt != null) compoundStmt.sem(tbl);
        tbl.closeScope();
    }

    public String getFunctionName() {
        return functionName;
    }

    public ArrayList<Expr<?>> getArgsList() {
        return args.getParamList(null);
    }

    public FuncType getFuncDefType() {
        return funcType;
    }
}
