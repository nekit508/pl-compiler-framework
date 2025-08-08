package com.github.nekit508.plcf.lang.exceptions;

public class ParseFail extends Throwable {
    public ParseFail() {
        super();
    }

    public ParseFail(String message) {
        super(message);
    }

    public ParseFail(String message, Throwable cause) {
        super(message, cause);
    }
}
