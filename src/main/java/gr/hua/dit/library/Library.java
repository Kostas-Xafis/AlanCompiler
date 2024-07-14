package gr.hua.dit.library;

import java.util.ArrayList;
import java.util.Arrays;

public class Library {

    public static ArrayList<String> functionNames = new ArrayList<>(
            Arrays.asList(
                WriteByte.name,
                WriteChar.name,
                WriteInteger.name,
                WriteString.name,
//                ReadByte.name,
                ReadChar.name,
                ReadInteger.name,
                ReadString.name,
                Strlen.name
//                Strcmp.name
            )
    );

    public static ArrayList<LangInternals> Functions(){
        return new ArrayList<>(
            Arrays.asList(
                new WriteByte(),
                new WriteChar(),
                new WriteInteger(),
                new WriteString(),
//                new ReadByte(),
                new ReadChar(),
                new ReadInteger(),
                new ReadString(),
                new StringToArrayList(),
                new Strlen()
//                new Strcmp()
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
