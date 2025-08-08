package com.github.nekit508.plcf.lang.utils;

import arc.util.Log;

public class Logger {
    public boolean enabled;

    public Logger(boolean enabled) {
        this.enabled = enabled;
    }
    public void info(String str, Object... objects) {
        if (enabled)
            Log.info(str, objects);
    }

}
