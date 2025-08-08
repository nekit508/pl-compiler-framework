package com.github.nekit508.plcf.lang;

public abstract class Tree {
    public TreeKind kind;
    public Tree(TreeKind kind) {
        this.kind = kind;
    }

    public abstract void accept(TreeWalker<?> analyzer);

    // TODO
    public void type() {}
}
