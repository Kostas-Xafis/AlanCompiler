package gr.hua.dit.compiler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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

    public static void main(String[] args) throws IOException {
        HashMap<String, String> argMap = parseArgs(args);
        Boolean willExecute = argMap.containsKey("-x");
        Boolean isTest = argMap.containsKey("-t");
        String path = argMap.get("-f");

        if (!path.endsWith(".alan")) {
            System.out.println("Incompatible file format. Please provide a .alan file.");
        } else {
            path = pathToAbsolute(path);
        }

        Reader reader = path == null ? new InputStreamReader(System.in)
                                : new InputStreamReader(Files.newInputStream(Paths.get(path)), StandardCharsets.UTF_8);
        Lexer lexer;
        try {
             lexer = new Lexer(reader);
        } catch (Exception e) {
            if (isTest)
                System.out.println(0);
            else
                System.err.println("Lexer Error: " + e.getMessage());
            return;
        }
        Parser parser = new Parser(lexer);

        try {
            Object result = parser.parse().value;
        } catch(Exception e) {
            if (isTest) {
                System.out.println(0);
            } else {
                System.err.println("Error: " + e);
            }
        } finally {
            ArrayList<AST<?>> astNodes = AST.getAllNodes();
            if (isTest) {
                ArrayList<AST<?>> hangingRoots = AST.getHangingRoots();
                if (hangingRoots.size() == 1 && hangingRoots.get(0).getType().equals("FuncDef")) {
                    System.out.println(1);
                } else {
                    System.out.println(0);
                }
            } else {
                System.out.println("Total ast nodes: " + astNodes.size() + "\n Nodes :" + astNodes);
                AST.getHangingRoots().forEach(root -> {
                    System.out.println(root.formattedString(0, Integer.MAX_VALUE));
                });
            }
        }
    }
}
