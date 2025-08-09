package com.github.nekit508.plcf.lang.compiletime.parser;

import arc.struct.Seq;
import com.github.nekit508.plcf.lang.Context;
import com.github.nekit508.plcf.lang.Tree;
import com.github.nekit508.plcf.lang.compiletime.token.DefaultTokenKinds;
import com.github.nekit508.plcf.lang.compiletime.token.Token;
import com.github.nekit508.plcf.lang.compiletime.token.TokenKind;
import com.github.nekit508.plcf.lang.exceptions.ParseFail;
import com.github.nekit508.plcf.lang.exceptions.ParserException;
import com.github.nekit508.plcf.lang.utils.Logger;
import com.github.nekit508.plcf.lang.utils.ReusableStream;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public abstract class Parser<T extends Context<?>> {
    public ReusableStream<Token> tokenStream;
    public T context;
    public Logger logger;

    public Parser(ReusableStream<Token> stream, T context) {
        this.tokenStream = stream;
        this.context = context;
        logger = new Logger(context.getRules().debugParser);
    }

    public abstract <O extends Tree, P extends Parser<T>> ParserRule<P, O> getRootRule();

    public Token current() {
        return tokenStream.get();
    }
    
    public void redo() {
        tokenStream.redo();
    }

    public void redo(int num) {
        tokenStream.redo(num);
    }

    public boolean probe(TokenKind... kinds) throws ParseFail {
        var token = next();

        for (TokenKind kind : kinds)
            if (kind == token.kind)
                return true;

        return false;
    }

    public boolean probeAndRedoIf(TokenKind... kinds) throws ParseFail {
        var out = probe(kinds);
        redo();
        return out;
    }

    public boolean probeAndRedoIf(boolean value, TokenKind... kinds) throws ParseFail {
        var out = probe(kinds);
        if (out == value)
            redo();
        return out;
    }

    public boolean probeNot(TokenKind... kinds) throws ParseFail {
        var token = next();

        for (TokenKind kind : kinds)
            if (kind == token.kind)
                return false;

        return true;
    }

    public boolean probeNotAndRedoIf(TokenKind... kinds) throws ParseFail {
        var out = probeNot(kinds);
        redo();
        return out;
    }

    public boolean probeNotAndRedoIf(boolean value, TokenKind... kinds) throws ParseFail {
        var out = probeNot(kinds);
        if (out == value)
            redo();
        return out;
    }

    public boolean hasAnyToken() throws ParseFail {
        var token = next();
        redo();

        return token.kind != DefaultTokenKinds.EOT;
    }

    public Token next() throws ParseFail {
        var token = tokenStream.next();

        if (token == null)
            throw new ParseFail("Token is null.");

        return token;
    }

    public Token accept(TokenKind... requiredTokenKinds) throws ParseFail {
        var token = next();

        for (TokenKind requiredTokenKind : requiredTokenKinds)
            if (requiredTokenKind == token.kind)
                return token;

        throw new ParseFail("Required any of " + new Seq<>(requiredTokenKinds) + ", but " + token.kind + " provided. At " + token.pos);
    }

    public Token except(TokenKind... exceptedTokenKinds) throws ParseFail {
        var token = next();

        for (TokenKind exceptedTokenKind : exceptedTokenKinds)
            if (exceptedTokenKind == token.kind)
                throw new ParseFail("Excepted all of " + new Seq<>(exceptedTokenKinds) + ", but " + token.kind + " provided. At " + token.pos);

        return token;
    }

    @SafeVarargs
    public final <R extends Tree, P extends Parser<T>> R parseAny(ParserRule<P, ? extends R>... rules) throws ParseFail {
        String causes = "";

        for (var rule : rules) {
            try {
                tokenStream.saveState();
                var out = parse(rule);
                tokenStream.disposeState();
                return out;
            } catch (ParseFail e) {
                tokenStream.redoState();
                var bytes = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(bytes));
                causes += "\n" + bytes;
            }
        }

        var pos = tokenStream.next().pos;
        redo();
        String out = "";
        for (var rule : rules)
            out += " " + rule.name;
        throw new ParseFail("Failed to parse any of " + out + " at " + pos + "." + causes);
    }

    public <R extends Tree, P extends Parser<T>> R parse(ParserRule<P, R> rule) throws ParseFail {
        var pos = next().pos;
        redo();
        try {
            logger.info("Parsing rule @ at @", rule.name, pos);
            return rule.parse((P) this);
        } catch (ParseFail e) {
            logger.info("Failed rule @ at @", rule.name, pos);
            throw new ParseFail("Failed to parse " + rule.name + " at " + pos + ".", e);
        }
    }

    public Tree parseCompileSource() throws ParserException {
        try {
            return parse(getRootRule());
        } catch (ParseFail e) {
            throw new ParserException(e);
        }
    }
}
