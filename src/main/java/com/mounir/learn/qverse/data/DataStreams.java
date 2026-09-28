package com.mounir.learn.qverse.data;

import com.mounir.learn.qverse.core.exception.TestDataException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** Opens a data file from the classpath first (portable in CI), then from the file system. */
final class DataStreams {

    private DataStreams() {
    }

    static InputStream open(String source) {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(source);
        if (in != null) {
            return in;
        }
        try {
            return Files.newInputStream(Path.of(source));
        } catch (IOException e) {
            throw new TestDataException("Test data not found on classpath or disk: " + source, e);
        }
    }
}
