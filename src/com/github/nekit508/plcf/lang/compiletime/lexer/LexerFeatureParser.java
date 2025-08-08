package com.github.nekit508.plcf.lang.compiletime.lexer;

import com.github.nekit508.plcf.lang.Context;
import com.github.nekit508.plcf.lang.exceptions.ParseFail;

public interface LexerFeatureParser<P extends Context<?>> {
    void internalParse(Lexer<P> cont) throws ParseFail;

    default void parse(Lexer<P> cont) throws ParseFail {
        internalParse(cont);
    }
}
