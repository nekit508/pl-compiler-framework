package com.github.nekit508.plcf.lang.compiletime;

import arc.struct.Seq;
import com.github.nekit508.plcf.lang.Context;
import com.github.nekit508.plcf.lang.Tree;
import com.github.nekit508.plcf.lang.exceptions.CompileException;
import com.github.nekit508.plcf.lang.runtime.Executable;
import com.github.nekit508.plcf.lang.utils.Logger;

public abstract class Compiler<T extends Context<?>> {
    public Logger logger;
    public T context;
    public Tree unit;

    public Compiler(Tree root, T context) {
        this.context = context;
        this.unit = root;
        logger = new Logger(context.getRules().debugCompiler);
    }

    public abstract Seq<Executable<T>> compile() throws CompileException;
}
