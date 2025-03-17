package shu.xai.材料.page;

public class treeNode {
treeNode left;
treeNode right;
boolean leaf;
int value;
  public treeNode(treeNode left, treeNode right, boolean leaf) {
    this.left = left;
    this.right = right;
    this.leaf = leaf;
    }

    public treeNode(treeNode left, treeNode right, boolean leaf, int value) {
        this.left = left;
        this.right = right;
        this.leaf = leaf;
        this.value = value;
    }

    public treeNode getLeft() {
        return left;
    }

    public void setLeft(treeNode left) {
        this.left = left;
    }

    public treeNode getRight() {
        return right;
    }

    public void setRight(treeNode right) {
        this.right = right;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
