package gr.hua.dit.compiler;

import gr.hua.dit.compiler.ast.FuncParams;
import gr.hua.dit.compiler.ast.Program;
import gr.hua.dit.compiler.symbol.SymbolTable;
import gr.hua.dit.compiler.types.DataType;
import gr.hua.dit.compiler.types.FuncType;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Main {

    private static String pathToAbsolute(String path) throws IOException {
        File file = new File(path);
        if (!file.isAbsolute()) {
            return file.getCanonicalPath();
        }
        return path;
    }

    private static HashMap<String, String> parseArgs(String[] args) {
        HashMap<String, String> argMap = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-") && i + 1 < args.length && !(args[i + 1].startsWith("-"))) {
                argMap.put(args[i], args[i + 1]);
            } else {
                argMap.put(args[i], null);
            }
        }
        return argMap;
    }

    public static void populateSymbolTable(SymbolTable tbl) {
        tbl.addEntry("writeString", new FuncType(
            new FuncParams("s", DataType.String(), true),
            DataType.Proc())
        );

        tbl.addEntry("writeInteger", new FuncType(
            new FuncParams("i", DataType.Int(), false),
            DataType.Proc())
        );

        tbl.addEntry("writeChar", new FuncType(
            new FuncParams("c", DataType.Char(), false),
            DataType.Proc())
        );

        tbl.addEntry("writeByte", new FuncType(
            new FuncParams("b", DataType.Byte(), false),
            DataType.Proc())
        );

        tbl.addEntry("readString", new FuncType(
            new FuncParams("n", DataType.Int(), false,
                new FuncParams("s", DataType.String(), true)),
            DataType.Proc())
        );

        tbl.addEntry("readInteger", new FuncType(DataType.Int()));

        tbl.addEntry("readChar", new FuncType(DataType.Char()));

        tbl.addEntry("readByte", new FuncType(DataType.Byte()));

        tbl.addEntry("strlen", new FuncType(
            new FuncParams("s", DataType.String(), true),
            DataType.Int())
        );

        tbl.addEntry("strcmp", new FuncType(
            new FuncParams("s1", DataType.String(), true,
                new FuncParams("s2", DataType.String(), true)),
            DataType.Int())
        );

        tbl.addEntry("strcat", new FuncType(
            new FuncParams("s1", DataType.String(), true,
                new FuncParams("s2", DataType.String(), true)),
            DataType.String())
        );

        tbl.addEntry("strcpy", new FuncType(
            new FuncParams("s1", DataType.String(), true,
                new FuncParams("s2", DataType.String(), true)),
            DataType.String())
        );

        tbl.addEntry("shrink", new FuncType(
            new FuncParams("i", DataType.Int(), false),
            DataType.Byte())
        );

        tbl.addEntry("extend", new FuncType(
            new FuncParams("b", DataType.Byte(), false),
            DataType.Int())
        );
    }

    public static void main(String[] args) throws IOException {
        HashMap<String, String> argMap = parseArgs(args);
        Boolean willExecute = argMap.containsKey("-x");
        Boolean isTest = argMap.containsKey("-t");
        Boolean hasInputFile = argMap.containsKey("-f");
        Boolean flattenAST = argMap.containsKey("-flat");
        String path = argMap.get("-f");

        if (hasInputFile && !path.endsWith(".alan")) {
            System.out.println("Incompatible file format. Please provide a .alan file.");
        } else if (hasInputFile){
            path = pathToAbsolute(path);
        }

        Reader reader = !hasInputFile ? new InputStreamReader(System.in)
                                : new InputStreamReader(Files.newInputStream(Paths.get(path)), StandardCharsets.UTF_8);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);

        Program astRoot = null;
        try {
            astRoot = (Program) parser.parse().value;
            SymbolTable tbl = new SymbolTable();
            populateSymbolTable(tbl);
            astRoot.sem(tbl);
            System.out.println(astRoot.formattedString(0, 100));
        } catch(Exception e) {
            if (!isTest) {
                System.err.println("Error: ");
                e.printStackTrace();
            }
        } finally {
//            if (astRoot != null) {
//                System.out.println(astRoot.formattedString(0, 50));
//            }
        }
    }
}
