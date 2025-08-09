package com.github.nekit508.plcf.lang.compiletime.parser;

import com.github.nekit508.plcf.lang.Context;
import com.github.nekit508.plcf.lang.Tree;
import com.github.nekit508.plcf.lang.exceptions.ParseFail;

public class ParserRule<P extends Parser<? extends Context<?>>, O extends Tree> {
    private final ParserRuleParser<P, O> parser;
    public String name;

    public ParserRule(String name, ParserRuleParser<P, O> parser) {
        this.name = name;
        this.parser = parser;
    }

    public O parse(P p) throws ParseFail {
        return parser.parse(p);
    }
}
