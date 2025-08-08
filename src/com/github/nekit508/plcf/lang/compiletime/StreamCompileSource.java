package com.github.nekit508.plcf.lang.compiletime;

import com.github.nekit508.plcf.lang.utils.ReusableCharStream;
import com.github.nekit508.plcf.lang.utils.ReusableStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamCompileSource implements CompileSource {
    private final InputStream stream;

    public StreamCompileSource(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public ReusableStream<Character> getInputStream() {
        return new ReusableCharStream(new InputStreamReader(stream), 512);
    }

    @Override
    public void dispose() {
        try {
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
