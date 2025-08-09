package com.github.nekit508.plcf.example;

import arc.func.Prov;
import com.github.nekit508.plcf.lang.compiletime.token.TokenKind;

public enum OneSymbolTokenKind implements TokenKind, Prov<Character> {
    L_BRACE('{'),
    R_BRACE('}'),

    L_BRACKET('['),
    R_BRACKET(']'),

    COLON(':'),

    COMMA(',');

    public final char symbol;

    OneSymbolTokenKind(char v) {
        this.symbol = v;
    }

    @Override
    public Character get() {
        return symbol;
    }
}
