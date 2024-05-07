package gr.hua.dit.compiler;

import java.util.ArrayList;
import java.util.function.Function;

public class AST<T>{
    private static final ArrayList<AST<?>> astArr = new ArrayList<>();
    private static int idCounter = 0;
    public int id;
    private String type;
    private T value;
    private AST<?> left;
    private AST<?> right;

    public AST(){}

    public AST(String type, T value, AST<?> left, AST<?> right) {
        this.id = idCounter++;
        this.type = type;
        this.value = value;
        this.left = left;
        this.right = right;
        astArr.add(this);
//        System.out.println("Added node: " + this + " \n");
    }

    public AST(String type, T value, AST<?> left) {
        this(type, value, left, null);
    }

    public AST(String type, T value) {
        this(type, value, null, null);
    }

    public Boolean hasChild(AST<?> node) {
        return (this.left != null && this.left.id == node.id) || (this.right != null && this.right.id == node.id);
    }

    public void traverse(Function<AST<?>, Void> f) {
        f.apply(this);
        if (left != null) {
            left.traverse(f);
        }
        if (right != null) {
            right.traverse(f);
        }
    }

    public String toString() {
        return "{ Type: " + type + ", Value: \"" + value + "\"" + (left != null ? ", Left: " + this.left.type : "") + (right != null ? ", Right: " + this.right.type : "")+ "}";
    }

    public String formattedString(Integer depth, Integer maxDepth) {
        if (maxDepth == 0) {
            return "node,\n";
        }
        String tabs = "";
        for (int i = 0; i < depth; i++) {
            tabs = tabs.concat("\t");
        }
        return "{\n" +
            tabs + "\tType: " + type + ",\n" +
            (value == null ? "" : tabs + "\tValue: \"" + value + "\",\n") +
            (left == null  ? "" : tabs + "\tLeft: " +  left.formattedString(depth + 1, maxDepth-1)) +
            (right == null ? "" : tabs + "\tRight: " + right.formattedString(depth + 1, maxDepth-1)) +
            tabs + "},\n" ;
    }

    public static ArrayList<AST<?>> getHangingRoots(){
        ArrayList<AST<?>> roots = new ArrayList<>();
        ArrayList<AST<?>> astArrcopy = new ArrayList<>(astArr);
        AST<?> currentRoot = astArrcopy.remove(0);

        while (!astArrcopy.isEmpty()) {
            boolean rootChanged = false;
            for (AST<?> ast : astArrcopy) {
                if (ast.hasChild(currentRoot)) {
                    // Change current root and remove any child nodes from the list
                    currentRoot = ast;
                    rootChanged = true;
                    currentRoot.traverse(node -> {
                        astArrcopy.remove(node);
                        return null;
                    });
                    break;
                }
            }
            if (!rootChanged || astArrcopy.isEmpty()) {
                roots.add(currentRoot);
                astArrcopy.remove(currentRoot);
                currentRoot = astArrcopy.isEmpty() ? null : astArrcopy.remove(0);
            }
        }
        return roots;
    }

    public static ArrayList<AST<?>> getAllNodes(){
        return astArr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public AST<?> getLeft() {
        return left;
    }

    public void setLeft(AST<?> left) {
        this.left = left;
    }

    public AST<?> getRight() {
        return right;
    }

    public void setRight(AST<?> right) {
        this.right = right;
    }
}

