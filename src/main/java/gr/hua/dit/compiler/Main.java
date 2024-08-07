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
        boolean willExecute = argMap.containsKey("-x");
        boolean hasInputFile = argMap.containsKey("-f");
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

            if (willExecute) {
//                System.out.println(astRoot.formattedString(0, 100));
                astRoot.compile(tbl, new File(path).getName());
            } else {
                System.out.println(astRoot.formattedString(0, 100));
            }
        } catch(Exception e) {
            System.err.println("Error: ");
            e.printStackTrace();
        }
    }
}
