package com.github.nekit508.plcf.lang.compiletime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileCompileSource extends StreamCompileSource {
    public FileCompileSource(File file) throws FileNotFoundException {
        super(new FileInputStream(file));
    }
}
