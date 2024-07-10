package gr.hua.dit.library;

import java.util.ArrayList;
import java.util.Arrays;

public class Library {

    public static ArrayList<String> functionNames = new ArrayList<>(
            Arrays.asList(
                "writeString",
                "writeChar",
                "writeInteger",
                "writeByte"
            )
    );

    public static ArrayList<LangInternals> Functions(){
        return new ArrayList<>(
            Arrays.asList(
                new WriteString(),
                new WriteChar(),
                new WriteInteger(),
                new WriteByte()
            )
        );
    }

    public static LangInternals getFunction(String name){
        for (LangInternals f : Functions()){
            if (f.getName().equals(name)){
                return f;
            }
        }
        return null;
    }
}
