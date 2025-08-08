package com.github.nekit508.plcf.example;

import com.github.nekit508.plcf.lang.compiletime.token.TokenKind;

public enum BaseTokenKind implements TokenKind {
    STRING,
    IDENT,
    NUMBER,
    BOOLEAN
}
