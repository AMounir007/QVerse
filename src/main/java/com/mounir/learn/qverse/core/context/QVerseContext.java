package com.mounir.learn.qverse.core.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Thread-scoped scenario context. Every TestNG worker thread gets its own isolated store,
 * which is what makes parallel execution safe (no static shared state between tests).
 * Use it to pass business data between steps: {@code QVerseContext.put("customerId", id)}.
 */
public final class QVerseContext {

    private static final ThreadLocal<Map<String, Object>> STORE = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<String> TEST_NAME = new ThreadLocal<>();

    private QVerseContext() {
    }

    public static void start(String testName) {
        STORE.get().clear();
        TEST_NAME.set(testName);
    }

    public static String testName() {
        return Optional.ofNullable(TEST_NAME.get()).orElse("unknown-test");
    }

    public static void put(String key, Object value) {
        STORE.get().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) STORE.get().get(key);
    }

    public static void clear() {
        STORE.remove();
        TEST_NAME.remove();
    }
}
