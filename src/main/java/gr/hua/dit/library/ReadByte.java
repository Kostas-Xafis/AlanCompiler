package gr.hua.dit.library;

import gr.hua.dit.compiler.ast.FuncParams;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.irgen.Descriptor;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;
import gr.hua.dit.compiler.types.Type;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ReadByte extends LangInternals {

    public static String name = "readByte";

    private Type type = new FuncType(DataType.Byte());

    public void compile(CompileContext cc) {
        String descriptor = Descriptor.build(type);
        MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, name, descriptor, null, null);
        mn.instructions.add(new InsnNode(Opcodes.ARETURN));
        mn.maxLocals = 1;
        mn.maxStack = 2;

        cc.getClassNode().methods.add(mn);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

//        tbl.addEntry("readString", new FuncType(
//            new FuncParams("n", DataType.Int(), false,
//        new FuncParams("s", DataType.String(), true)),
//        DataType.Proc())
//        );
//
//        tbl.addEntry("readInteger", new FuncType(DataType.Int()));
//
//        tbl.addEntry("readChar", new FuncType(DataType.Char()));
//
//        tbl.addEntry("readByte", new FuncType(DataType.Byte()));
//
//        tbl.addEntry("strlen", new FuncType(
//            new FuncParams("s", DataType.String(), true),
//        DataType.Int())
//        );
//
//        tbl.addEntry("strcmp", new FuncType(
//            new FuncParams("s1", DataType.String(), true,
//        new FuncParams("s2", DataType.String(), true)),
//        DataType.Int())
//        );
//
//        tbl.addEntry("strcat", new FuncType(
//            new FuncParams("s1", DataType.String(), true,
//        new FuncParams("s2", DataType.String(), true)),
//        DataType.String())
//        );
//
//        tbl.addEntry("strcpy", new FuncType(
//            new FuncParams("s1", DataType.String(), true,
//        new FuncParams("s2", DataType.String(), true)),
//        DataType.String())
//        );
//
//        tbl.addEntry("shrink", new FuncType(
//            new FuncParams("i", DataType.Int(), false),
//        DataType.Byte())
//        );
//
//        tbl.addEntry("extend", new FuncType(
//            new FuncParams("b", DataType.Byte(), false),
//        DataType.Int())
//        );
}
