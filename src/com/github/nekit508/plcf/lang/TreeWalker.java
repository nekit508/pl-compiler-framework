package com.github.nekit508.plcf.lang;

import arc.struct.Seq;

public abstract class TreeWalker<T extends Context<?>> {
    public Seq<Tree> depthStack = new Seq<>();
    public T context;

    public TreeWalker(T context) {
        this.context = context;
    }

    public void enter(Tree tree) {
        depthStack.add(tree);
    }

    public void exit(Tree tree) {
        depthStack.pop();
    }

    public <R extends Tree> R as(Tree tree) {
        return (R) tree;
    }
}
