package com.github.nekit508.plcf.example;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import com.github.nekit508.plcf.lang.Tree;
import com.github.nekit508.plcf.lang.TreeWalker;
import com.github.nekit508.plcf.lang.TreeKind;

public abstract class BaseTree extends Tree {
    public BaseTree(TreeKind kind) {
        super(kind);
    }

    public static class Key extends Tree {
        public String ident;

        public Key(TreeKind kind, String ident) {
            super(kind);
            this.ident = ident;
        }

        @Override
        public void accept(TreeWalker<?> analyzer) {
            analyzer.enter(this);
            analyzer.exit(this);
        }
    }

    public static abstract class Value extends Tree {
        public Value(TreeKind kind) {
            super(kind);
        }
    }

    public static class Array extends Value {
        public Seq<Value> values;

        public Array(TreeKind kind, Seq<Value> values) {
            super(kind);
            this.values = values;
        }

        @Override
        public void accept(TreeWalker<?> analyzer) {
            analyzer.enter(this);
            analyzer.exit(this);
        }
    }

    public static class Str extends Value {
        public String value;

        public Str(TreeKind kind, String value) {
            super(kind);
            this.value = value;
        }

        @Override
        public void accept(TreeWalker<?> analyzer) {
            analyzer.enter(this);
            analyzer.exit(this);
        }
    }

    public static class Number extends Value {
        public Object value;

        public Number(TreeKind kind, Object value) {
            super(kind);
            this.value = value;
        }

        @Override
        public void accept(TreeWalker<?> analyzer) {
            analyzer.enter(this);
            analyzer.exit(this);
        }
    }

    public static class Bool extends Value {
        public boolean value;

        public Bool(TreeKind kind, boolean value) {
            super(kind);
            this.value = value;
        }

        @Override
        public void accept(TreeWalker<?> analyzer) {
            analyzer.enter(this);
            analyzer.exit(this);
        }
    }

    public static class Map extends Value {
        public ObjectMap<Key, Value> data;

        public Map(TreeKind kind, ObjectMap<Key, Value> data) {
            super(kind);
            this.data = data;
        }

        @Override
        public void accept(TreeWalker<?> analyzer) {
            analyzer.enter(this);
            analyzer.exit(this);
        }
    }
}
