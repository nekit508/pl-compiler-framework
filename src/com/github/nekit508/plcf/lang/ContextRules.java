package com.github.nekit508.plcf.lang;

public class ContextRules {
    public boolean debugLexer;
    public boolean debugParser;
    public boolean debugCompiler;

    public ContextRules(boolean debugLexer, boolean debugParser, boolean debugCompiler) {
        this.debugLexer = debugLexer;
        this.debugParser = debugParser;
        this.debugCompiler = debugCompiler;
    }
}
