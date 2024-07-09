package gr.hua.dit.compiler.irgen;

public class IRHelper {
    private static int tempCounter = 0;
    private static int labelCounter = 0;

    public static Address newTemp() {
        tempCounter++;
        return new VarAddress("t" + tempCounter);
    }

    public static LabelAddress newLabel() {
        labelCounter++;
        return new LabelAddress("L" + labelCounter);
    }
}
