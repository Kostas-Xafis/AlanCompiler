package gr.hua.dit.compiler.ast;

import gr.hua.dit.compiler.irgen.Address;

import java.util.ArrayList;
import java.util.function.Function;

public class ASTNode<T> {
    private static int idCounter = 0;
    private int id;

    private String name;
    private T value;
    private ASTNode<?> left;
    private ASTNode<?> right;
    private ArrayList<ASTNode> children;

    public Address addr;

    public ASTNode(String name, T value, ASTNode<?> left, ASTNode<?> right) {
        this.id = idCounter++;
        this.name = name;
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public ASTNode(String name, T value, ArrayList<ASTNode> children) {
        this.id = idCounter++;
        this.name = name;
        this.value = value;
        this.children = children;
    }

    public ASTNode(String name, T value, ASTNode<?> left) {
        this(name, value, left, null);
    }

    public ASTNode(String name, T value) {
        this(name, value, null, null);
    }

    public ASTNode(String name) {
        this(name, null, null, null);
    }

    public Boolean hasChild(ASTNode<?> node) {
        return node.isEqual(this.left) || node.isEqual(this.right) || (this.children != null && this.children.contains(node));
    }

    public void traverse(Function<ASTNode<?>, Void> f) {
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

    public ArrayList<ASTNode<?>> getNodes(String nodeName) {
        ArrayList<ASTNode<?>> nodes = new ArrayList<>();
        this.traverse(n -> {
            if (n.name.equals(nodeName)) {
                nodes.add(n);
            }
            return null;
        });
        return nodes;
    }


    public String toString() {
        return "{ Name: " + name + ", Value: \"" + value + "\"" + (left != null ? ", Left: " + this.left.name : "") + (right != null ? ", Right: " + this.right.name : "")+ "}";
    }

    public String formattedString(Integer depth, Integer maxDepth) {
        if (maxDepth == 0) {
            return this.name + ",\n";
        }
        String tabs = "";
        for (int i = 0; i < depth; i++) {
            tabs = tabs.concat("\t");
        }
        String finalTabs = tabs;
        return "{\n".concat(
            tabs + "\tName: " + name + ",\n" +
                (value == null ? "" : tabs + "\tValue: \"" + value + "\",\n") +
                (this instanceof Expr ? tabs + "\tType: " + ((Expr<?>) this).getInferredType() + ",\n" : "") +
                (this instanceof VariableDef ? tabs + "\tType: " + ((VariableDef) this).getVariableType() + ",\n" : "") +
                (this instanceof FuncDef ? tabs + "\tType: " + ((FuncDef) this).getFuncDefType() + ",\n" : "") +
                (left == null  ? "" : (tabs + "\tLeft: ").concat(left.formattedString(depth + 1, maxDepth-1))) +
                (right == null ? "" : (tabs + "\tRight: ").concat(right.formattedString(depth + 1, maxDepth-1))) +
                (children == null ? "" : (tabs + "\tChildren: [\n").concat(
                    children.stream().map(child -> finalTabs + "\t\t" + child.formattedString(depth + 2, maxDepth-1)).reduce("", String::concat)
                ).concat(tabs + "\t],\n")) +
                tabs + "},\n") ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public ASTNode<?> getLeft() {
        return left;
    }

    public void setLeft(ASTNode<?> left) {
        this.left = left;
    }

    public ASTNode<?> getRight() {
        return right;
    }

    public void setRight(ASTNode<?> right) {
        this.right = right;
    }

    public void setChildren(ArrayList<ASTNode> children) {
        this.children = children;
    }

    public ArrayList<ASTNode> getChildren() {
        return children;
    }

    public int getId() {
        return id;
    }

    public boolean isEqual(ASTNode<?> node) {
        if (node == null) {
            return false;
        }
        return this.id == node.id;
    }

    public Address getAddress() {
        return addr;
    }

    public void codegen() {}
}
