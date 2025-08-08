package com.github.nekit508.plcf.lang.utils;

import arc.struct.IntSeq;
import com.github.nekit508.plcf.lang.compiletime.lexer.Lexer;

public class PosProviderCharReusableStreamWrapper extends ReusableStreamWrapper<Character> implements PosProvider {
    public char[] notVisible = {
            '\n', '\r'
    };

    public PosProviderCharReusableStreamWrapper(ReusableStream<Character> stream) {
        super(stream);
    }

    public Pos currentPos = new Pos(0, 0);
    public IntSeq linesSizes = new IntSeq();

    @Override
    public Pos getCurrentPos() {
        return currentPos;
    }

    @Override
    public void redo() {
        var c = get();

        if (!Lexer.in(c, notVisible))
            currentPos.pos -= 1;

        if(c == '\n') {
            currentPos.pos = linesSizes.pop();
            currentPos.line -= 1;
        }

        super.redo();
    }

    @Override
    public Character next() {
        var c = super.next();

        if (!Lexer.in(c, notVisible))
            currentPos.pos += 1;

        if(c == '\n') {
            linesSizes.add(currentPos.pos);
            currentPos.line += 1;
            currentPos.pos = 0;
        }

        return c;
    }
}
