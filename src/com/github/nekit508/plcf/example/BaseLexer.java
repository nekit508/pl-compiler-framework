package com.github.nekit508.plcf.example;

import arc.struct.Seq;
import com.github.nekit508.plcf.lang.compiletime.lexer.Lexer;
import com.github.nekit508.plcf.lang.compiletime.lexer.LexerRule;
import com.github.nekit508.plcf.lang.compiletime.token.TokenKind;
import com.github.nekit508.plcf.lang.utils.PosProvider;
import com.github.nekit508.plcf.lang.utils.ReusableStream;

public class BaseLexer extends Lexer<BaseContext> {
    public static char[] whitespace = {
            ' ', '\n', '\r', '\t'
    };

    public static char[] newLine = {
            '\n'
    };

    public static char[] identStart = {
            'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y',
            'Z',
            'a', 'b', 'c', 'd', 'e',
            'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y',
            'z',
            '$', '_'
    };
    public static char[] identBody = {
            'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y',
            'Z',
            'a', 'b', 'c', 'd', 'e',
            'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y',
            'z',
            '$', '_',
            '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9'
    };

    public static char[] numberStart = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };
    public static char[] numberEnd = {
            'b', 'i', 'l', 's', 'f', 'd',
            'B', 'I', 'L', 'S', 'F', 'D'
    };
    public static char[] numberBody = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    public static char[] stringQuotes = {
            '\'', '"'
    };

    public static Seq<LexerRule<BaseLexer>> rootRules = new Seq<>();

    public static String[] keywords = {
            "true",
            "false"
    };
    public static TokenKind[] keywordKinds = {
            BaseTokenKind.BOOLEAN,
            BaseTokenKind.BOOLEAN
    };

    public static LexerRule<BaseLexer>
            skipWhiteSpacesRule,
            skipCommentsRule,
            parseIdentOrKeywordRule,
            skipNotParsableSymbolsRule,
            parseStringRule,
            parseNumberRule,
            parseSingleSymbolTokenRule;

    static {
        parseSingleSymbolTokenRule = LexerRule.createOneSymbolParser("parseSingleSymbolToken", OneSymbolTokenKind.values());
        parseNumberRule = LexerRule.createNumberParser("parseNumber", BaseTokenKind.NUMBER, numberStart, numberBody, numberEnd);
        parseStringRule = LexerRule.createStringParser("parseString", BaseTokenKind.STRING, stringQuotes);
        skipWhiteSpacesRule = LexerRule.createSkip("skipWhiteSpaces", whitespace);
        skipCommentsRule = LexerRule.createSkipComments("skipComments", newLine, new char[]{'/'}, new char[]{'*'});
        parseIdentOrKeywordRule = LexerRule.createIdentOrKeywordParser("parseIdentOrKeyword", BaseTokenKind.IDENT, identStart, identBody, keywords, keywordKinds);
        skipNotParsableSymbolsRule = LexerRule.parseWhileParsing("skipNotParsableSymbols", skipCommentsRule, skipWhiteSpacesRule);

        rootRules.addAll(skipNotParsableSymbolsRule, parseSingleSymbolTokenRule, parseIdentOrKeywordRule, parseNumberRule, parseStringRule);
    }

    public BaseLexer(ReusableStream<Character> compileSource, PosProvider posProvider, BaseContext context) {
        super(compileSource, posProvider, context);
    }

    @Override
    public Seq<LexerRule<BaseLexer>> getRootRules() {
        return rootRules;
    }
}
