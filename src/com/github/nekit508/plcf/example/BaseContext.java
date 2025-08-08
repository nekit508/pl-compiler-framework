package com.github.nekit508.plcf.example;

import arc.struct.Seq;
import com.github.nekit508.plcf.lang.Context;
import com.github.nekit508.plcf.lang.ContextRules;
import com.github.nekit508.plcf.lang.Tree;
import com.github.nekit508.plcf.lang.TreeWalker;
import com.github.nekit508.plcf.lang.compiletime.*;
import com.github.nekit508.plcf.lang.compiletime.lexer.Lexer;
import com.github.nekit508.plcf.lang.compiletime.parser.Parser;
import com.github.nekit508.plcf.lang.compiletime.token.Token;
import com.github.nekit508.plcf.lang.utils.PosProvider;
import com.github.nekit508.plcf.lang.utils.PosProviderCharReusableStreamWrapper;
import com.github.nekit508.plcf.lang.utils.ReusableStream;

public class BaseContext extends Context<ContextRules> {
    public BaseContext(ContextRules rules) {
        super(rules);
    }

    @Override
    public <T extends CompileTask<?>, O extends CompileSource> T getCompileTaskFor(O input) {
        return (T) new CompileTask<>(input, this);
    }

    @Override
    public <T extends com.github.nekit508.plcf.lang.compiletime.Compiler<?>> T getCompiler(Tree root) {
        return (T) new BaseCompiler(this, root);
    }

    @Override
    public <T extends Parser<?>> T getParser(ReusableStream<Token> stream) {
        return (T) new BaseParser(stream, this);
    }

    @Override
    public <T extends Lexer<?>> T getLexer(ReusableStream<Character> stream) {
        var newStream = stream instanceof PosProvider ? stream : new PosProviderCharReusableStreamWrapper(stream);
        return (T) new BaseLexer(newStream, (PosProvider) newStream, this);
    }

    @Override
    public <T extends TreeWalker<?>> Seq<T> getAnalyzers() {
        return new Seq<>();
    }
}
