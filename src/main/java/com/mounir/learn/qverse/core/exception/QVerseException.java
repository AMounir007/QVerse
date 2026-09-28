package com.mounir.learn.qverse.core.exception;

/**
 * Root of the QVerse exception hierarchy. Unchecked so business tests stay free of try/catch noise;
 * messages are written for testers (what failed + likely fix), not only for developers.
 */
public class QVerseException extends RuntimeException {
    public QVerseException(String message) {
        super(message);
    }

    public QVerseException(String message, Throwable cause) {
        super(message, cause);
    }
}
