package gr.hua.dit.compiler;

import gr.hua.dit.compiler.ast.ASTNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AST<T>{
    private static final ArrayList<ASTNode<?>> astArr = new ArrayList<>();
    private ArrayList<AST<?>> children;

    public AST(){}

    public void add(ASTNode<?> node) {
        astArr.add(node);
    }

    public static ArrayList<ASTNode<?>> getHangingRoots(ArrayList<ASTNode<?>> astArrInp){
        ArrayList<ASTNode<?>> roots = new ArrayList<>();
        Set<ASTNode<?>> astSet = astArrInp == null ? new HashSet<ASTNode<?>>(astArr) : new HashSet<ASTNode<?>>(astArrInp);
        ASTNode<?> currentRoot = (ASTNode<?>) (astSet.toArray()[0]);

        while (!astSet.isEmpty()) {
            boolean rootChanged = false;
            boolean isRoot = false;
            for (ASTNode<?> ast : astSet) {
                if (ast.hasChild(currentRoot)) {
                    // Change current root and remove any child nodes from the list
                    currentRoot = ast;
                    rootChanged = true;
                    currentRoot.traverse(node -> {
                        astSet.remove(node);
                        return null;
                    });
                    break;
                }
            }
            if (!rootChanged || astSet.isEmpty()) {
                roots.add(currentRoot);
                if (!rootChanged) {
                    currentRoot.traverse(node -> {
                        astSet.remove(node);
                        return null;
                    });
                }
                currentRoot = astSet.isEmpty() ? null : (ASTNode<?>) (astSet.toArray()[0]);
            }
        }
        return roots;
    }

    public static ArrayList<ASTNode<?>> getHangingRoots(){
        return getHangingRoots(null);
    }

    public static ArrayList<ASTNode<?>> getAllNodes(){
        return astArr;
    }
}

