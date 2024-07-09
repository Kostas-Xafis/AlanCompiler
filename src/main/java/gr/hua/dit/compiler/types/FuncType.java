package gr.hua.dit.compiler.types;

import gr.hua.dit.compiler.ast.ExprList;
import gr.hua.dit.compiler.ast.FuncParams;

import java.util.ArrayList;

public class FuncType extends Type {

    private FuncParams params;
    private ExprList argsList;
    private Type res;

    public FuncType(FuncParams args, Type resultType) {
        if (args != null) {
            this.params = args;
        }
        this.res = resultType;
    }

    public FuncType(ExprList args, Type res) {
        if (args != null) {
            this.argsList = args;
        }
        this.res = res;
    }

    public FuncType(Type res) {
        this.res = res;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Function (");
        if (params != null) {
            ArrayList<Type> argTypes = params.getParamTypes(null);
            for (int i = 0; i < argTypes.size(); i++) {
                sb.append(argTypes.get(i).toString());
                if (i < argTypes.size() - 1) sb.append(", ");
            }
        }
        if (argsList != null) {
            ArrayList<Type> argTypes = argsList.getTypeList(null);
            for (int i = 0; i < argTypes.size(); i++) {
                sb.append(argTypes.get(i).toString());
                if (i < argTypes.size() - 1) sb.append(", ");
            }
        }
        sb.append(") -> ");
        sb.append(res != null ? res.toString() : "proc");
        return sb.toString();
    }

    public ArrayList<Type> getArgTypes() {
        if (params != null) {
            return params.getParamTypes(null);
        } else if (argsList != null) {
            return argsList.getTypeList(null);
        }
        return new ArrayList<>();
    }

    public Type getResult() { return res; }

    public void setResult(Type res) {
        this.res = res;
    }

    public boolean equals(Type t) {
        if (t instanceof FuncType) {
            ArrayList<Type> thatArgs = ((FuncType) t).getArgTypes();
            ArrayList<Type> args = getArgTypes();
            if (args != null) {
                // check that each argument type matches
                if (args.size() != thatArgs.size() || !this.res.equals(thatArgs)) {
                    return false;
                }
                for (int i = 0; i < args.size(); i++) {
                    if (!args.get(i).equals(thatArgs.get(i))) {
                        return false;
                    }
                }
                return true;
            }
            return this.res.equals(((FuncType) t).getResult());
        }
        return false;
    }

    public boolean resultEquals(FuncType t) {
        return this.res.equals(t.getResult());
    }

    public FuncType copy() {
        return new FuncType(params, res != null ? res.copy() : null);
    }
}
