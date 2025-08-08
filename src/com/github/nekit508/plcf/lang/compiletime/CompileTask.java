package com.github.nekit508.plcf.lang.compiletime;

import com.github.nekit508.plcf.lang.Context;
import com.github.nekit508.plcf.lang.TreeWalker;
import com.github.nekit508.plcf.lang.exceptions.CompileTaskException;
import com.github.nekit508.plcf.lang.runtime.Executable;

public class CompileTask<T extends Context<?>> {
    public CompileSource compileSource;
    public T context;

    public CompileTask(CompileSource compileSource, T context) {
        this.compileSource = compileSource;
        this.context = context;
    }

    public void run() throws CompileTaskException {
        try {
            var lexer = context.getLexer(compileSource.getInputStream());
            var parser = context.getParser(lexer);

            var root = parser.parseCompileSource();

            var analyzers = context.getAnalyzers();
            for (TreeWalker<?> analyzer : analyzers)
                root.accept(analyzer);

            var compiler = context.getCompiler(root);
            var executables = compiler.compile();

            for (Executable<?> executable : executables)
                context.addExecutable(executable);
        } catch (Throwable e) {
            throw new CompileTaskException(e);
        }
    }
}
