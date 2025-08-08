package com.github.nekit508.plcf.lang;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Disposable;
import com.github.nekit508.plcf.lang.compiletime.*;
import com.github.nekit508.plcf.lang.compiletime.lexer.Lexer;
import com.github.nekit508.plcf.lang.compiletime.parser.Parser;
import com.github.nekit508.plcf.lang.compiletime.token.Token;
import com.github.nekit508.plcf.lang.runtime.Executable;
import com.github.nekit508.plcf.lang.utils.ReusableStream;

/** Class that contains static symbols. */
public abstract class Context<R extends ContextRules> implements Disposable {
    public R rules;
    public ObjectMap<String, Executable<?>> executablesMap = new ObjectMap<>();
    public Seq<Executable<?>> executables = new Seq<>();

    public Context(R rules) {
        this.rules = rules;
    }

    public <T extends R> T getRules() {
        return (T) rules;
    }

    public <T extends Executable<?>> T getExecutableByName(String name) {
        return (T) executablesMap.get(name);
    }

    /** WARNING !!! MUTABLE !!! */
    public <T extends Executable<?>> Seq<T> getExecutables() {
        return executables.as();
    }

    public <T extends Executable<?>> void addExecutable(T executable) {
        executablesMap.put(executable.name, executable);
        executables.add(executable);
    }

    public abstract <T extends CompileTask<?>, O extends CompileSource> T getCompileTaskFor(O input);

    public abstract <T extends com.github.nekit508.plcf.lang.compiletime.Compiler<?>> T getCompiler(Tree root);
    public abstract <T extends Parser<?>> T getParser(ReusableStream<Token> stream);
    public abstract <T extends Lexer<?>> T getLexer(ReusableStream<Character> stream);
    public abstract <T extends TreeWalker<?>> Seq<T> getAnalyzers();

    @Override
    public void dispose() {
        executables.clear();
        executablesMap.clear();
    }
}
