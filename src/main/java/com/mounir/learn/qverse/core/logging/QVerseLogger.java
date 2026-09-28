package com.mounir.learn.qverse.core.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.time.LocalTime;

/**
 * Thin wrapper over Log4J2 that ALSO buffers every message per thread, so the listener can attach
 * exactly the log of one test to its Allure result (no interleaving from parallel threads).
 */
public final class QVerseLogger {

    private static final ThreadLocal<StringBuilder> BUFFER = ThreadLocal.withInitial(StringBuilder::new);
    private final Logger log;

    private QVerseLogger(Class<?> type) {
        this.log = LogManager.getLogger(type);
    }

    public static QVerseLogger getLogger(Class<?> type) {
        return new QVerseLogger(type);
    }

    public void info(String msg, Object... args) {
        log.info(msg, args);
        record("INFO", msg, args);
    }

    public void debug(String msg, Object... args) {
        log.debug(msg, args);
    }

    public void warn(String msg, Object... args) {
        log.warn(msg, args);
        record("WARN", msg, args);
    }

    public void error(String msg, Throwable t) {
        log.error(msg, t);
        record("ERROR", msg + " -> " + t, new Object[0]);
    }

    private static void record(String level, String msg, Object[] args) {
        BUFFER.get().append(LocalTime.now()).append(" [").append(level).append("] ")
                .append(ParameterizedMessage.format(msg, args)).append(System.lineSeparator());
    }

    /** Returns and resets the log captured for the current thread/test. */
    public static String drainThreadLog() {
        String text = BUFFER.get().toString();
        BUFFER.remove();
        return text;
    }
}
