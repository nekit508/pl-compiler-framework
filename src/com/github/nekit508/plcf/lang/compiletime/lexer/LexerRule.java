package com.github.nekit508.plcf.lang.compiletime.lexer;

import arc.func.Prov;
import arc.util.Nullable;
import com.github.nekit508.plcf.lang.Context;
import com.github.nekit508.plcf.lang.compiletime.token.TokenKind;
import com.github.nekit508.plcf.lang.exceptions.ParseFail;

public class LexerRule<L extends Lexer<? extends Context<?>>> {
    public LexerRuleParser<L> parser;
    public @Nullable LexerRuleAcceptor<L> acceptor;
    public String name;

    public LexerRule(String name, LexerRuleParser<L> parser, @Nullable LexerRuleAcceptor<L> acceptor) {
        this.name = name;
        this.parser = parser;
        this.acceptor = acceptor;
    }

    public void parse(L context) throws ParseFail {
        parser.parse(context);
    }

    public boolean canBeParsed(L context) throws ParseFail {
        return acceptor == null || acceptor.canBeProcessed(context);
    }

    public static boolean in(char c, char[] group) {
        for (char value : group)
            if (c == value)
                return true;

        return false;
    }

    public static <C extends Context<?>, L extends Lexer<C>, T extends TokenKind & Prov<Character>> LexerRule<L> createOneSymbolParser(String name, T[] kinds) {
        return new LexerRule<>(name, lexer -> {
            for (T kind: kinds) {
                if (kind.get() == lexer.getChar()) {
                    lexer.ct().kind = kind;
                    lexer.tokenParsed();
                    return;
                }
            }
        }, null);
    }

    public static <C extends Context<?>, L extends Lexer<C>> LexerRule<L> createStringParser(String name, TokenKind kind, char[] quotes) {
        return new LexerRule<>(name, lexer -> {
            var ind = -1;
            var c = lexer.getChar();
            for (int i = 0; i < quotes.length; i++)
                if (c == quotes[i])
                    ind = i;

            if (ind == -1)
                throw new ParseFail("String rule approved as parsable, but no staring quotes founded.");

            while (lexer.getNextChar() != quotes[ind])
                lexer.putChar(lexer.getChar());

            lexer.ct().kind = kind;
            lexer.tokenParsed();
        }, lexer -> in(lexer.getChar(), quotes));
    }

    public static <C extends Context<?>, L extends Lexer<C>> LexerRule<L> createSkip(String name, char[] toSkip) {
        return new LexerRule<>(name, lexer -> {
            while (in(lexer.getNextChar(), toSkip));
            lexer.setTokenPos();
        }, lexer -> in(lexer.getChar(), toSkip));
    }

    public static <C extends Context<?>, L extends Lexer<C>> LexerRule<L> createSkipComments(String name, char[] newLine, char[] commentSymbol, char[] multiLineCommentModification) {
        return new LexerRule<>(name, lexer -> {
            lexer.saveCharState();

            if (!in(lexer.getChar(), commentSymbol)) {
                lexer.redoCharState();
                lexer.setTokenPos();
                return;
            }

            if (in(lexer.getNextChar(), commentSymbol)) {
                lexer.disposeCharState();

                while (!in(lexer.getNextChar(), newLine));
                lexer.redoChar();

                lexer.setTokenPos();
                return;
            } else lexer.redoChar();

            if (in(lexer.getNextChar(), multiLineCommentModification)) {
                while (true) {
                    if (in(lexer.getNextChar(), multiLineCommentModification))
                        if (in(lexer.getNextChar(), commentSymbol)) {
                            lexer.getNextChar();
                            lexer.disposeCharState();
                            lexer.setTokenPos();
                            return;
                        } else lexer.redoChar();
                }
            }

            lexer.redoCharState();
        }, lexer -> in(lexer.getChar(), commentSymbol));
    }

    public static <C extends Context<?>, L extends Lexer<C>> LexerRule<L> createNumberParser(String name, TokenKind kind, char[] start, char[] body, char[] end) {
        return new LexerRule<>(name, lexer -> {
            lexer.ct().kind = kind;

            do lexer.putChar(lexer.getChar());
            while (in(lexer.getNextChar(), body));

            if (in(lexer.getChar(), end))
                lexer.putChar(lexer.getChar());
            else
                lexer.ct().kind = null;

            lexer.tokenParsed();
        }, lexer -> in(lexer.getChar(), start));
    }

    public static <C extends Context<?>, L extends Lexer<C>> LexerRule<L> createIdentOrKeywordParser(String name, TokenKind identKind, char[] start, char[] body, String[] keywords, TokenKind[] keywordsKinds) {
        return new LexerRule<>(name, lexer -> {
            do lexer.putChar(lexer.getChar());
            while (in(lexer.getNextChar(), body));
            lexer.redoChar();

            lexer.ct().kind = identKind;

            String str = lexer.cl().toString();
            for (int i = 0; i < keywords.length; i++)
                if (keywords[i].equals(str))
                    lexer.ct().kind = keywordsKinds[i];

            lexer.tokenParsed();
        }, lexer -> in(lexer.getChar(), start));
    }

    public static <C extends Context<?>, L extends Lexer<C>> LexerRule<L> parseWhileParsing(String name, LexerRule<L>... features) {
        return new LexerRule<>(name, lexer -> {
            whileLoop: while (true) {
                for (var feature : features) {
                    if (lexer.parseIfCan(feature))
                        continue whileLoop;
                }
                break;
            }
        }, lexer -> {
            for (LexerRule<L> feature : features) {
                if (lexer.canParse(feature))
                    return true;
            }
            return false;
        });
    }
}
