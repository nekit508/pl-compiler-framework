package com.github.nekit508.plcf.lang.compiletime.parser;

import com.github.nekit508.plcf.lang.Context;
import com.github.nekit508.plcf.lang.Tree;
import com.github.nekit508.plcf.lang.exceptions.ParseFail;

public class ParserFeature<P extends Context<?>, O extends Tree> {
    private final ParserFeatureParser<P, O> parser;
    public String name;

    public ParserFeature(String name, ParserFeatureParser<P, O> parser) {
        this.name = name;
        this.parser = parser;
    }

    public O parse(Parser<P> context) throws ParseFail {
        return parser.parse(context);
    }
}
