package com.github.nekit508.plcf.lang.compiletime.lexer;

import arc.util.Nullable;
import com.github.nekit508.plcf.lang.Context;
import com.github.nekit508.plcf.lang.exceptions.ParseFail;

public class LexerFeature<P extends Context<?>> {
    public LexerFeatureParser<P> parser;
    public @Nullable LexerFeatureAcceptor<P> acceptor;
    public String name;

    public LexerFeature(String name, LexerFeatureParser<P> parser, @Nullable LexerFeatureAcceptor<P> acceptor) {
        this.name = name;
        this.parser = parser;
        this.acceptor = acceptor;
    }

    public void parse(Lexer<P> context) throws ParseFail {
        parser.parse(context);
    }

    public boolean canBeParsed(Lexer<P> context) throws ParseFail {
        return acceptor == null || acceptor.canBeProcessed(context);
    }
}
