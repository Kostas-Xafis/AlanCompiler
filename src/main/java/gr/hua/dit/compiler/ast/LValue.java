package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.symbol.SymbolEntry;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Optional;

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
        Optional<SymbolEntry> entry = tbl.lookup(getName());
        if (!entry.isPresent()) {
            throw SemanticException.UndefinedVariableException(getName());
        }
        DataType type = (DataType) entry.get().getType();
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
            if (expr instanceof ConstInt) {
                int index = ((ConstInt) expr).getValue();
                // Array have length of 0 when
                if (type.getArraySize() != 0 && index >= type.getArraySize()) {
                        throw SemanticException.ArrayIndexOutOfBoundsException(tbl, getName(), index);
                }
            } else if (!(expr instanceof Mops) && expr instanceof Expr && expr.getValue() == "-") {
                throw SemanticException.ArrayIndexMustBePositiveException(getName());
            }
        }
    }

    public void compile(CompileContext cc) throws CompilerException {
        compile(cc, "load");
    }

    public void compile(CompileContext cc, String action) throws CompilerException {
        System.out.println("Compiling LValue: " + name + " with action: " + action);
        if (expr != null) {
            expr.compile(cc);
        }
//        if (action.equals("load")) {
//            cc.addInsn(expr == null ? cc.getLoadInsn(getName()) : cc.getArrayLoadInsn(getName()));
//        } else if (action.equals("store")) {
//            cc.addInsn(expr == null ? cc.getStoreInsn(getName()) : cc.getArrayStoreInsn(getName()));
//        }
        if (action.equals("load")) {
            cc.loadLocalInt(name);
        } else if (action.equals("store")) {
            cc.storeLocalInt(name);
//            cc.addInsn(new VarInsnNode(Opcodes.ISTORE, 400));
        }
    }
}
