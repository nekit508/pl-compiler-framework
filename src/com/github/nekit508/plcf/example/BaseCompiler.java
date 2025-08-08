package com.github.nekit508.plcf.example;

import arc.struct.Seq;
import com.github.nekit508.plcf.lang.Tree;
import com.github.nekit508.plcf.lang.compiletime.Compiler;
import com.github.nekit508.plcf.lang.exceptions.CompileException;
import com.github.nekit508.plcf.lang.runtime.Executable;

public class BaseCompiler extends Compiler<BaseContext> {
    public BaseCompiler(BaseContext context, Tree root) {
        super(root, context);
    }

    @Override
    public Seq<Executable<BaseContext>> compile() throws CompileException {
        return new Seq<>();
    }
}
