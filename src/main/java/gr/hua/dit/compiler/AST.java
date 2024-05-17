package gr.hua.dit.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class AST<T>{
    private static final ArrayList<AST<?>> astArr = new ArrayList<>();
    private static int idCounter = 0;
    private int id;
    private String type;
    private T value;
    private AST<?> left;
    private AST<?> right;
    private ArrayList<AST<?>> children;

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

    public AST(String type) {
        this(type, null, null, null);
    }

    public Boolean hasChild(AST<?> node) {
        return node.isEqual(this.left) || node.isEqual(this.right) || (this.children != null && this.children.contains(node));
    }

    public void traverse(Function<AST<?>, Void> f) {
        f.apply(this);
        if (left != null) {
            left.traverse(f);
        }
        if (right != null) {
            right.traverse(f);
        }
        if (children != null) {
            children.forEach(child -> child.traverse(f));
        }
    }

    // Like traverse but stops when it finds a node that is not of the specified type
    public void traverseBranch(String type, Consumer<AST<?>> f) {
        if (this.type.equals(type)) {
            f.accept(this);
        } else {
            return ;
        }
        if (left != null) {
            left.traverseBranch(type, f);
        }
        if (right != null) {
            right.traverseBranch(type, f);
        }
        if (children != null) {
            children.forEach(child -> child.traverseBranch(type, f));
        }
    }

    public static ArrayList<AST<?>> getHangingRoots(ArrayList<AST<?>> astArrInp){
        ArrayList<AST<?>> roots = new ArrayList<>();
        Set<AST<?>> astSet = astArrInp == null ? new HashSet<AST<?>>(astArr) : new HashSet<AST<?>>(astArrInp);
//        System.out.println("AST Set: " + astSet);
        AST<?> currentRoot = (AST<?>) (astSet.toArray()[0]);

        while (!astSet.isEmpty()) {
            boolean rootChanged = false;
            boolean isRoot = false;
            for (AST<?> ast : astSet) {
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
                currentRoot = astSet.isEmpty() ? null : (AST<?>) (astSet.toArray()[0]);
            }
        }
        return roots;
    }

    public static ArrayList<AST<?>> getHangingRoots(){
        return getHangingRoots(null);
    }

    public static ArrayList<AST<?>> getNodes(String type, AST<?> node) {
        ArrayList<AST<?>> nodes = new ArrayList<>();
        if (node == null) {
            astArr.forEach(ast -> {
                if (ast.type.equals(type)) {
                    nodes.add(ast);
                }
            });
        } else {
            node.traverse(node1 -> {
                if (node1.type.equals(type)) {
                    nodes.add(node1);
                }
                return null;
            });
        }
        return nodes;
    }

    public static ArrayList<AST<?>> getNodes(String type) {
        return AST.getNodes(type, null);
    }

    public void flatten(String type, Function<AST<?>, Void> f){
        if (this.type.equals(type)) {
            f.apply(this);
        }
        if (left != null) {
            this.left.flatten(type, f);
        }
        if (right != null) {
            this.right.flatten(type, f);
        }
        if (children != null) {
            children.forEach(child -> child.flatten(type, f));
        }
    }

    public static ArrayList<AST<?>> getAllNodes(){
        return astArr;
    }

    public void deref(){
        AST.astArr.remove(this);
    }

    public String toString() {
        return "{ Type: " + type + ", Value: \"" + value + "\"" + (left != null ? ", Left: " + this.left.type : "") + (right != null ? ", Right: " + this.right.type : "")+ "}";
    }

    public String formattedString(Integer depth, Integer maxDepth) {
        if (maxDepth == 0) {
            return this.type + ",\n";
        }
        String tabs = "";
        for (int i = 0; i < depth; i++) {
            tabs = tabs.concat("\t");
        }
        String finalTabs = tabs;
        return "{\n".concat(
            tabs + "\tType: " + type + ",\n" +
                (value == null ? "" : tabs + "\tValue: \"" + value + "\",\n") +
                (left == null  ? "" : (tabs + "\tLeft: ").concat(left.formattedString(depth + 1, maxDepth-1))) +
                (right == null ? "" : (tabs + "\tRight: ").concat(right.formattedString(depth + 1, maxDepth-1))) +
                (children == null ? "" : (tabs + "\tChildren: [\n").concat(
                    children.stream().map(child -> (finalTabs + "\t\t").concat(child.formattedString(depth + 2, maxDepth-1))).reduce("", (acc, child) -> acc.concat(child))
                ).concat(tabs + "\t],\n")) +
                tabs + "},\n") ;
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

    public int getId() {
        return id;
    }

    public ArrayList<AST<?>> getChildren() {
        return children;
    }

    public void addChildren(AST<?> ...childArr) {
        if (children == null) {
            children = new ArrayList<>();
        }
        if (childArr.length == 1) {
            children.add(childArr[0]);
            return;
        }
        children.addAll(Arrays.asList(childArr));
    }
    public void setChildren(ArrayList<AST<?>> children) {
        this.children = children;
    }

    public boolean isEqual(AST<?> node) {
        if (node == null) {
            return false;
        }
        return this.id == node.id;
    }
}

