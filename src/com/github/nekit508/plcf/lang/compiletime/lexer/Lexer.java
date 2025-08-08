package com.github.nekit508.plcf.lang.compiletime.lexer;

import arc.struct.Seq;
import com.github.nekit508.plcf.lang.Context;
import com.github.nekit508.plcf.lang.compiletime.token.DefaultTokenKinds;
import com.github.nekit508.plcf.lang.compiletime.token.Token;
import com.github.nekit508.plcf.lang.exceptions.ParseFail;
import com.github.nekit508.plcf.lang.exceptions.TokenizerException;
import com.github.nekit508.plcf.lang.utils.*;

public abstract class Lexer<T extends Context<?>> extends ReusableStreamAbstractImpl<Token> {
    public ReusableStream<Character> reader;
    public PosProvider posProvider;
    public T context;
    public StringBuilder cl = new StringBuilder();
    public Token ct;
    public Logger logger;

    public boolean tokenParsed;

    public abstract Seq<LexerFeature<T>> getRootFeatures();

    public Lexer(ReusableStream<Character> compileSource, PosProvider posProvider, T context) {
        this.context = context;
        this.posProvider = posProvider;
        reader = compileSource;
        logger = new Logger(context.getRules().debugLexer);
    }

    @Override
    public Token readNextObject() {
        try {
            return parseNextToken();
        } catch (TokenizerException e) {
            throw new TokenizerException.UnhandledTokenizerException(e);
        }
    }

    public void saveCharState() {
        reader.saveState();
    }

    public void disposeCharState() {
        reader.disposeState();
    }

    public void redoCharState() {
        reader.redoState();
    }

    public char getChar() {
        return reader.get();
    }

    public void putChar(char c) {
        cl.append(c);
    }

    public void redoChar() {
        reader.redo();
    }

    public char getNextChar() {
        return reader.next();
    }

    public Pos pos() {
        return posProvider.getCurrentPos();
    }

    public void tokenParsed() {
        tokenParsed = true;
    }

    public Token ct() {
        return ct;
    }

    public StringBuilder cl() {
        return cl;
    }

    public void setTokenPos() {
        ct().pos.set(pos());
    }

    public Token parseNextToken() throws TokenizerException {
        try {
            tokenParsed = false;
            ct = new Token();
            cl.setLength(0);

            var c = getNextChar();

            setTokenPos();

            var features = getRootFeatures();
            for (LexerFeature<T> feature : features) {
                try {
                    parseIfCan(feature);
                    if (tokenParsed)
                        break;
                } catch (ParseFail fail) {
                    throw new TokenizerException(fail, pos());
                }
            }

            logger.info(ct.kind + "");

            if (ct.kind == null) {
                if (c == 65279)
                    throw new TokenizerException("Probably you are using UTF with BOM that is not supported.", pos());
                throw new TokenizerException("Unknown token kind.", pos());
            } else {
                ct.literal = cl.toString();
                return ct;
            }
        } catch (EOSException e) {
            ct.kind = DefaultTokenKinds.EOT;
            ct.literal = "\000";
            return ct;
        }
    }

    public static boolean in(char c, char[] group) {
        for (char value : group)
            if (c == value)
                return true;

        return false;
    }

    public boolean canParse(LexerFeature<T> feature) throws ParseFail {
        try {
            logger.info("Check can parse feature @ at @", feature.name, pos());
            return feature.canBeParsed(this);
        } catch (ParseFail e) {
            logger.info("Failed to check can parse feature @ at @", feature.name, pos());
            throw new ParseFail("Failed check can parse parse " + feature.name + " at " + pos() + ".", e);
        }
    }

    public boolean parseIfCan(LexerFeature<T> feature) throws ParseFail {
        if (canParse(feature)) {
            parse(feature);
            return true;
        }

        return false;
    }

    public void parse(LexerFeature<T> feature) throws ParseFail {
        try {
            logger.info("Parsing feature @ at @", feature.name, pos());
            feature.parse(this);
        } catch (ParseFail e) {
            logger.info("Failed feature @ at @", feature.name, pos());
            throw new ParseFail("Failed to parse " + feature.name + " at " + pos() + ".", e);
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        if (reader != null) {
            reader.dispose();
            reader = null;
        }

        cl.setLength(0);
        ct = null;
    }
}
