package com.github.nekit508.plcf.example;

import arc.struct.Seq;
import com.github.nekit508.plcf.lang.compiletime.lexer.Lexer;
import com.github.nekit508.plcf.lang.compiletime.lexer.LexerFeature;
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

    public static char[] stringStart = {'\'', '"'};

    public static Seq<LexerFeature<BaseContext>> rootFeatures = new Seq<>();

    public static String[] keywords = {
            "true",
            "false"
    };
    public static TokenKind[] keywordKinds = {
            BaseTokenKind.BOOLEAN,
            BaseTokenKind.BOOLEAN
    };

    public static LexerFeature<BaseContext>
            skipWhiteSpacesFeature,
            skipCommentsFeature,
            parseIdentOrKeywordFeature,
            skipNotParsableSymbolsFeature,
            parseStringFeature,
            parseNumberFeature,
            parseSingleSymbolTokenFeature;

    static {
        parseSingleSymbolTokenFeature = new LexerFeature<>("parseSingleSymbolToken", lexer -> {
            var kinds = OneSymbolTokenKind.values();
            for (OneSymbolTokenKind kind : kinds) {
                if (kind.symbol == lexer.getChar()) {
                    lexer.ct().kind = kind;
                    lexer.tokenParsed();
                    return;
                }
            }
        }, null);

        parseNumberFeature = new LexerFeature<>("parseNumber", lexer -> {
            lexer.ct().kind = BaseTokenKind.NUMBER;

            do lexer.putChar(lexer.getChar());
            while (in(lexer.getNextChar(), numberBody));

            if (in(lexer.getChar(), numberEnd))
                lexer.putChar(lexer.getChar());
            else
                lexer.ct().kind = null;

            lexer.tokenParsed();
        }, lexer -> in(lexer.getChar(), numberStart));

        parseStringFeature = new LexerFeature<>("parseString", lexer -> {
            var doubleQuotes = lexer.getChar() == '"';

            while (lexer.getNextChar() != (doubleQuotes ? '"' : '\''))
                lexer.putChar(lexer.getChar());

            lexer.ct().kind = BaseTokenKind.STRING;
            lexer.tokenParsed();
        }, lexer -> in(lexer.getChar(), stringStart));

        skipWhiteSpacesFeature = new LexerFeature<>("skipWhiteSpaces", lexer -> {
            while (in(lexer.getNextChar(), whitespace));
            lexer.setTokenPos();
        }, lexer -> in(lexer.getChar(), whitespace));

        skipCommentsFeature = new LexerFeature<>("skipComments", lexer -> {
            lexer.saveCharState();

            if (lexer.getChar() != '/') {
                lexer.redoCharState();
                lexer.setTokenPos();
                return;
            }

            if (lexer.getNextChar() == '/') {
                lexer.disposeCharState();

                while (!in(lexer.getNextChar(), newLine));
                lexer.redoChar();

                lexer.setTokenPos();
                return;
            } else lexer.redoChar();

            if (lexer.getNextChar() == '*') {
                while (lexer.getNextChar() != '*');

                if (lexer.getNextChar() == '/') {
                    lexer.disposeCharState();
                    lexer.setTokenPos();
                    return;
                }
            }

            lexer.redoCharState();
        }, lexer -> lexer.getChar() == '/');

        parseIdentOrKeywordFeature = new LexerFeature<>("parseIdentOrKeyword", lexer -> {
            do lexer.putChar(lexer.getChar());
            while (in(lexer.getNextChar(), identBody));
            lexer.redoChar();

            lexer.ct().kind = BaseTokenKind.IDENT;

            String str = lexer.cl().toString();
            for (int i = 0; i < keywords.length; i++)
                if (keywords[i].equals(str))
                    lexer.ct().kind = keywordKinds[i];

            lexer.tokenParsed();
        }, lexer -> in(lexer.getChar(), identStart));

        skipNotParsableSymbolsFeature = new LexerFeature<>("skipNotParsableSymbols", lexer -> {
            while (lexer.canParse(skipCommentsFeature) || lexer.canParse(skipWhiteSpacesFeature)) {
                if (!lexer.parseIfCan(skipWhiteSpacesFeature))
                    lexer.parseIfCan(skipCommentsFeature);
            }
        }, lexer -> lexer.canParse(skipCommentsFeature) || lexer.canParse(skipWhiteSpacesFeature));

        rootFeatures.addAll(skipNotParsableSymbolsFeature, parseSingleSymbolTokenFeature, parseIdentOrKeywordFeature, parseNumberFeature, parseStringFeature);
    }

    public BaseLexer(ReusableStream<Character> compileSource, PosProvider posProvider, BaseContext context) {
        super(compileSource, posProvider, context);
    }

    @Override
    public Seq<LexerFeature<BaseContext>> getRootFeatures() {
        return rootFeatures;
    }
}
