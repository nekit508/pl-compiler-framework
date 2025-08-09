package com.github.nekit508.plcf.lang.compiletime.lexer;

import com.github.nekit508.plcf.lang.Context;
import com.github.nekit508.plcf.lang.exceptions.ParseFail;

public interface LexerRuleAcceptor<L extends Lexer<? extends Context<?>>> {
    boolean canBeProcessed(L lexer) throws ParseFail;
}
