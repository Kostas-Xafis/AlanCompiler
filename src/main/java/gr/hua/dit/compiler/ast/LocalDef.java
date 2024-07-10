package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.Type;

public class LocalDef extends ASTNode {

    private final String defName;
    private final ASTNode<?> def;
    private final LocalDef next;
    private Type type;

    public LocalDef(FuncDef f, LocalDef ld) {
        super("LocalDef", null, ld, f);
        def = f;
        defName = f.getFunctionName();
        type = f.getFuncDefType();
        next = ld;
    }

    public LocalDef(VariableDef v, LocalDef ld) {
        super("LocalDef", null, ld, v);
        def = v;
        defName = v.getVariableName();
        type = v.getVariableType();
        next = ld;
    }

    @Override
    public String toString() {
        return "LocalDef(" + def + ")";
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        if (def instanceof FuncDef) {
            ((FuncDef) def).sem(tbl);
        } else if (def instanceof VariableDef) {
            ((VariableDef) def).sem(tbl);
        }
        if (next != null) {
            next.sem(tbl);
        }
    }

    public String getDefName() {
        return defName;
    }

    public void compile(CompileContext cc) throws CompilerException {
        def.compile(cc);
        if (next != null) {
            next.compile(cc);
        }
    }
}
