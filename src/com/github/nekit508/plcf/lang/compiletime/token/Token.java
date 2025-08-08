package com.github.nekit508.plcf.lang.compiletime.token;

import arc.util.Strings;
import com.github.nekit508.plcf.lang.utils.Pos;

public class Token {
    public String literal;
    public Pos pos = new Pos(0, 0);
    public TokenKind kind;

    @Override
    public String toString() {
        return Strings.format("{[@]@ at @}", kind, literal, pos);
    }

}
