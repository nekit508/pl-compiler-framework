package com.github.nekit508.plcf.lang.compiletime.lexer;

import com.github.nekit508.plcf.lang.Context;
import com.github.nekit508.plcf.lang.exceptions.ParseFail;

public interface LexerFeatureAcceptor<P extends Context<?>> {
    boolean canBeProcessed(Lexer<P> cont) throws ParseFail;
}
