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

    private static void flattenLocalDef() {
        ArrayList<AST<?>> LocalDefRoot = AST.getHangingRoots(AST.getNodes("LocalDef"));
        LocalDefRoot.forEach(root -> {
            root.traverseBranch("LocalDef", (node) -> {
                root.addChildren(node.getLeft());
                if (root != node) node.deref();
            });
            root.setLeft(null);
            root.setRight(null);
        });
    }

    private static void flattenExprList() {
        ArrayList<AST<?>> ExprListRoot = AST.getHangingRoots(AST.getNodes("ExprList"));
        ExprListRoot.forEach(root -> {
            root.traverseBranch("ExprList", (node) -> {
                root.addChildren(node.getLeft());
                if (root != node) node.deref();
            });
            root.setLeft(null);
            root.setRight(null);
        });
    }

    private static void flattenFuncParams() {
        ArrayList<AST<?>> FuncParamsRoot = AST.getHangingRoots(AST.getNodes("FuncParams"));
        FuncParamsRoot.forEach(root -> {
            root.addChildren(new AST<>("FuncParam", root.getValue(), root.getLeft()));
            root.traverseBranch("FuncParams", (node) -> {
                if (root != node) {
                    root.addChildren(node);
                    node.deref();
                }
            });
            root.setValue(null);
            root.setLeft(null);
            root.setRight(null);
            root.getChildren().forEach(node -> {
                node.setRight(null);
            });
        });
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

//        long startParse = System.currentTimeMillis();
        try {
            AST<?> rootRes = (AST<?>) parser.parse().value;
            if (flattenAST) {
                flattenLocalDef();
                flattenExprList();
                flattenFuncParams();
            }
        } catch(Exception e) {
            if (!isTest) {
                System.err.println("Error: " + e);
            }
        } finally {
//            long endParse = System.currentTimeMillis();
//            System.out.println("Time to parse: " + (endParse - startParse) + "ms");

            ArrayList<AST<?>> astNodes = AST.getAllNodes();

//            long start = System.currentTimeMillis();
            ArrayList<AST<?>> hangingRoots = AST.getHangingRoots();
//            long end = System.currentTimeMillis();

            if (isTest) {
                // If there is only one hanging root and it is a function definition, then the program is correctly parsed
                if (hangingRoots.size() == 1 && hangingRoots.get(0).getType().equals("FuncDef")) {
                    System.out.println(0);
                } else {
                    System.out.println(1);
                }
            } else {
                System.out.println("Total ast nodes: " + astNodes.size());
//                System.out.println("Nodes : " + astNodes);
                hangingRoots.forEach(root -> {
                    System.out.println(root.formattedString(0, 35));
                });
            }
//            System.out.println("Time to find hanging roots: " + (end - start) + "ms");
        }
    }
}
