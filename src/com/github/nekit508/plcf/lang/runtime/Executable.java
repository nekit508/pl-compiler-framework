package com.github.nekit508.plcf.lang.runtime;

import com.github.nekit508.plcf.lang.Context;

/** Returns object that can be somehow executed. */
public abstract class Executable<T extends Context<?>> {
    protected T context;
    public String name;

    public Executable(T context, String name) {
        this.name = name;
        this.context = context;
    }

    public abstract void execute();
}
