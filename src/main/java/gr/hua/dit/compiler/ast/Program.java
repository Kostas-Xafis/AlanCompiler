package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.errors.SemanticException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.library.LangInternals;
import gr.hua.dit.library.Library;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class Program extends ASTNode {

    private final FuncDef main;

    public Program(FuncDef main) {
        super("Program", null, main);
        this.main = main;
    }

    public void sem(SymbolTable tbl) throws SemanticException {
        for (LangInternals func : Library.Functions()) {
            tbl.addEntry(func.getName(), func.getType());
        }

        if(!main.getFuncDefType().getResult().equals(DataType.ProcType)) {
            throw SemanticException.MainFunctionMustReturnProcException();
        };
        main.sem(tbl);
    }

    public void compile(SymbolTable tbl, String sourceFile) throws IOException, CompilerException {
        System.out.println("Compiling " + sourceFile);
        ClassNode cn = new ClassNode();
        CompileContext context = new CompileContext(cn);
        context.setSymbolTable(tbl);
        cn.access = Opcodes.ACC_PUBLIC;
        cn.version = Opcodes.V1_8;
        cn.name = "MiniBasic";
        cn.sourceFile = sourceFile;
        cn.superName = "java/lang/Object";
        cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
            "theVars", "[I" , null, null));

        MethodNode mn;
        {
            // constructor
            mn = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V"));
            mn.instructions.add(new InsnNode(Opcodes.RETURN));
            mn.maxLocals = 1;
            mn.maxStack = 1;
            cn.methods.add(mn);
        }
        {
            // static initializer
            mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            mn.instructions.add(new VarInsnNode(Opcodes.BIPUSH, 25));
            mn.instructions.add(new VarInsnNode(Opcodes.NEWARRAY, Opcodes.T_INT));
            mn.instructions.add(new FieldInsnNode(Opcodes.PUTSTATIC, cn.name, "theVars", "[I"));
            mn.instructions.add(new InsnNode(Opcodes.RETURN));
            mn.maxLocals = 1;
            mn.maxStack = 1;
            cn.methods.add(mn);
        }

        context.setClassNode(cn);

        for (LangInternals func : Library.Functions()) {
            try {
                func.compile(cn);
            } catch (UnsupportedOperationException e) {
                System.err.println(e.getMessage() + " in " + func.getName());
            }
        }

        mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);

        context.setCurrentMethodNode(mn);

        if (!main.getFunctionName().equals("main")) {
            MethodNode mn2 = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, main.getFunctionName(), main.getDescriptor(), null, null);
            context.addInsn(new MethodInsnNode(Opcodes.INVOKESTATIC, cn.name, main.getFunctionName(), main.getDescriptor(), false));
            context.addInsn(new InsnNode(Opcodes.RETURN));
            context.setCurrentMethodNode(mn2);
        }

        main.compile(context);

        context.addInsn(new InsnNode(Opcodes.RETURN));
//        mn.maxLocals = context.getNumberOfTemps();
        mn.maxLocals = 20;
        mn.maxStack = 128;
        cn.methods.add(mn);

        try {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
            TraceClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out));
            cn.accept(cv);

            byte code[] = cw.toByteArray();

            FileOutputStream fos = new FileOutputStream("./MiniBasic.class");
            fos.write(code);
            fos.close();
        } catch (Exception e) {
            TraceClassVisitor displayCv = new TraceClassVisitor(cn, new PrintWriter(System.out));
            cn.accept(displayCv);
            e.printStackTrace();
        }
    }
}
