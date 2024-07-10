package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.errors.CompilerException;
import gr.hua.dit.compiler.irgen.CompileContext;
import gr.hua.dit.compiler.types.DataType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.VarInsnNode;

public class ConstChar extends Expr<DataType> {
    private final String value;

    public ConstChar(String value) {
        super(DataType.Char(), value);
        this.value = value.subSequence(1, value.length() - 1).toString();
        this.setName("Char_Const");
    }

    public String toString() {
        return "ConstChar(" + value.toString() + ")";
    }

    public void compile(CompileContext cc) throws CompilerException {
        // In case of single letter chars
        System.out.println("Compiling ConstChar: " + value + " with length: " + value.length());
        if (value.length() == 1) {
            cc.addInsn(new VarInsnNode(Opcodes.BIPUSH, value.charAt(0)));
        } else if (value.startsWith("\\") && value.charAt(1) != 'x') {
            // In case of escape characters
            switch (value) {
                case "\\n":
                    cc.addInsn(new VarInsnNode(Opcodes.BIPUSH, '\n'));
                    break;
                case "\\t":
                    cc.addInsn(new VarInsnNode(Opcodes.BIPUSH, '\t'));
                    break;
                case "\\r":
                    cc.addInsn(new VarInsnNode(Opcodes.BIPUSH, '\r'));
                    break;
                case "\\0":
                    cc.addInsn(new VarInsnNode(Opcodes.BIPUSH, '\0'));
                    break;
                case "\\\\":
                    cc.addInsn(new VarInsnNode(Opcodes.BIPUSH, '\\'));
                    break;
                case "\\\'":
                    cc.addInsn(new VarInsnNode(Opcodes.BIPUSH, '\''));
                    break;
                case "\\\"":
                    cc.addInsn(new VarInsnNode(Opcodes.BIPUSH, '\"'));
                    break;
                default:
                    throw new CompilerException("Invalid character constant: " + value);
            }
        } else if (value.length() > 2) {
            // Hex handling will be the last thing to do
            // In hex format like \x00
            if (value.charAt(0) == '\\' && value.charAt(1) == 'x') {
                try {
                    int hexValue = Integer.parseInt(value.substring(2), 16);
                    cc.addInsn(new VarInsnNode(Opcodes.BIPUSH, hexValue));
                } catch (NumberFormatException e) {
                    throw new CompilerException("Invalid character constant: " + value);
                }
            } else {
                throw new CompilerException("Invalid character constant: " + value);
            }
        } else {
            throw new CompilerException("Invalid character constant: " + value);
        }
    }

    @Override
    public String getValue() {
        return value;
    }
}
