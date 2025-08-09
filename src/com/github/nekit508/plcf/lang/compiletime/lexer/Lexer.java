package com.github.nekit508.plcf.lang.compiletime.lexer;

import arc.struct.Seq;
import com.github.nekit508.plcf.lang.Context;
import com.github.nekit508.plcf.lang.compiletime.parser.Parser;
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

    public abstract <L extends Lexer<T>> Seq<LexerRule<L>> getRootRules();

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

            var rules = getRootRules();
            for (var rule : rules) {
                try {
                    parseIfCan(rule);
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

    public <L extends Lexer<T>> boolean canParse(LexerRule<L> rule) throws ParseFail {
        try {
            logger.info("Check can parse rule @ at @", rule.name, pos());
            return rule.canBeParsed((L) this);
        } catch (ParseFail e) {
            logger.info("Failed to check can parse rule @ at @", rule.name, pos());
            throw new ParseFail("Failed check can parse parse " + rule.name + " at " + pos() + ".", e);
        }
    }

    public <L extends Lexer<T>> boolean parseIfCan(LexerRule<L> rule) throws ParseFail {
        if (canParse(rule)) {
            parse(rule);
            return true;
        }

        return false;
    }

    public <L extends Lexer<T>>  void parse(LexerRule<L> rule) throws ParseFail {
        try {
            logger.info("Parsing rule @ at @", rule.name, pos());
            rule.parse((L) this);
        } catch (ParseFail e) {
            logger.info("Failed rule @ at @", rule.name, pos());
            throw new ParseFail("Failed to parse " + rule.name + " at " + pos() + ".", e);
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
