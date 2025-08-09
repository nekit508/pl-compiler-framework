package com.github.nekit508.plcf.lang.compiletime.parser;

import com.github.nekit508.plcf.lang.Context;
import com.github.nekit508.plcf.lang.Tree;
import com.github.nekit508.plcf.lang.exceptions.ParseFail;

public interface ParserRuleParser<P extends Parser<? extends Context<?>>, O extends Tree> {
    O internalParse(P parser) throws ParseFail;

    default O parse(P parser) throws ParseFail {
        return internalParse(parser);
    }
}
