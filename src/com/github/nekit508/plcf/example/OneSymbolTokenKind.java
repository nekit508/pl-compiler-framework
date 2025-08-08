package com.github.nekit508.plcf.example;

import com.github.nekit508.plcf.lang.compiletime.token.TokenKind;

public enum OneSymbolTokenKind implements TokenKind {
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

}
