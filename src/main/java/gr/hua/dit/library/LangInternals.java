package gr.hua.dit.library;

import gr.hua.dit.compiler.types.Type;
import org.objectweb.asm.tree.ClassNode;

public abstract class LangInternals {

    private String name;

    private Type type;

    public void compile(ClassNode cw) {
        throw new UnsupportedOperationException("Not supported yet.");
    };

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

}
