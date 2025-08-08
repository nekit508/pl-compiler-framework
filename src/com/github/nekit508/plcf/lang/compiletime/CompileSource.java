package com.github.nekit508.plcf.lang.compiletime;

import arc.util.Disposable;
import com.github.nekit508.plcf.lang.utils.ReusableStream;

/** Class that represents something that contains characters for tokenizing. */
public interface CompileSource extends Disposable {
    ReusableStream<Character> getInputStream();
}
