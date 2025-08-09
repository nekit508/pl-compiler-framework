package com.github.nekit508.plcf.example;

import com.github.nekit508.plcf.lang.ContextRules;
import com.github.nekit508.plcf.lang.compiletime.FileCompileSource;
import com.github.nekit508.plcf.lang.exceptions.CompileTaskException;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        try {
            var context = new BaseContext(new ContextRules(true, false, false));
            context.getCompileTaskFor(new FileCompileSource(new File("file"))).run();
        } catch (CompileTaskException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
