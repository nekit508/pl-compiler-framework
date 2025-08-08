package com.github.nekit508.plcf.lang.compiletime.parser;

import com.github.nekit508.plcf.lang.Context;
import com.github.nekit508.plcf.lang.Tree;
import com.github.nekit508.plcf.lang.exceptions.ParseFail;

public interface ParserFeatureParser<P extends Context<?>, O extends Tree> {
    O internalParse(Parser<P> cont) throws ParseFail;

    default O parse(Parser<P> cont) throws ParseFail {
        return internalParse(cont);
    }
}
