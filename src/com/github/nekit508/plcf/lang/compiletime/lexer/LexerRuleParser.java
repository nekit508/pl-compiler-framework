package com.github.nekit508.plcf.lang.compiletime.lexer;

import com.github.nekit508.plcf.lang.Context;
import com.github.nekit508.plcf.lang.exceptions.ParseFail;

public interface LexerRuleParser<L extends Lexer<? extends Context<?>>> {
    void internalParse(L lexer) throws ParseFail;

    default void parse(L lexer) throws ParseFail {
        internalParse(lexer);
    }
}
