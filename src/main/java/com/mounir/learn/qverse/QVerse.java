package com.mounir.learn.qverse;

import com.mounir.learn.qverse.api.ApiActions;
import com.mounir.learn.qverse.config.ConfigManager;
import com.mounir.learn.qverse.config.QVerseConfig;
import com.mounir.learn.qverse.core.ai.AiExtensions;
import com.mounir.learn.qverse.core.context.QVerseContext;
import com.mounir.learn.qverse.data.TestDataFactory;
import com.mounir.learn.qverse.mobile.MobileActions;
import com.mounir.learn.qverse.web.WebActions;

import java.util.List;
import java.util.Map;

/**
 * <b>Facade</b> - the single entry point of the ecosystem. Beginners only need to remember "QVerse.":
 * <pre>
 * QVerse.web().open(url).click(LOGIN);
 * QVerse.api().get("/users/1").verifyStatusCode(200);
 * QVerse.mobile().tap(SUBMIT);
 * QVerse.data("testdata/users.json");
 * </pre>
 */
public final class QVerse {

    private QVerse() {
    }

    public static WebActions web() { return new WebActions(); }

    public static ApiActions api() { return new ApiActions(); }

    public static MobileActions mobile() { return new MobileActions(); }

    public static QVerseConfig config() { return ConfigManager.config(); }

    public static List<Map<String, Object>> data(String source) { return TestDataFactory.rows(source, ""); }

    public static <T> T data(String source, Class<T> type) { return TestDataFactory.load(source, type); }

    public static void remember(String key, Object value) { QVerseContext.put(key, value); }

    public static <T> T recall(String key) { return QVerseContext.get(key); }

    /** AI-ready: converts manual test steps into a QVerse test skeleton. */
    public static String generateTest(String name, List<String> steps) {
        return AiExtensions.testGenerator().generate(name, steps);
    }
}
