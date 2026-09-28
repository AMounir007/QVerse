package com.mounir.learn.qverse.reporting;

import com.mounir.learn.qverse.config.ConfigManager;
import com.mounir.learn.qverse.config.QVerseConfig;
import com.mounir.learn.qverse.core.logging.QVerseLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/** Publishes environment metadata and failure categories so the Allure dashboard is self-describing. */
public final class AllureEnvironmentWriter {

    private static final QVerseLogger LOG = QVerseLogger.getLogger(AllureEnvironmentWriter.class);

    private AllureEnvironmentWriter() {
    }

    public static Path resultsDir() {
        return Path.of(System.getProperty("allure.results.directory", "target/allure-results"));
    }

    public static void write() {
        QVerseConfig c = ConfigManager.config();
        Path dir = resultsDir();
        try {
            Files.createDirectories(dir);
            Properties p = new Properties();
            p.setProperty("Framework", "QVerse");
            p.setProperty("Environment", c.env());
            p.setProperty("Base URL", String.valueOf(c.baseUrl()));
            p.setProperty("API URL", String.valueOf(c.apiBaseUrl()));
            p.setProperty("Browser", c.browser().name());
            p.setProperty("Headless", String.valueOf(c.headless()));
            p.setProperty("Execution Mode", c.executionMode().name());
            p.setProperty("Java", System.getProperty("java.version"));
            p.setProperty("OS", System.getProperty("os.name"));
            try (var out = Files.newOutputStream(dir.resolve("environment.properties"))) {
                p.store(out, "QVerse execution environment");
            }
            try (InputStream categories = AllureEnvironmentWriter.class.getResourceAsStream("/allure/categories.json")) {
                if (categories != null) {
                    Files.copy(categories, dir.resolve("categories.json"), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            LOG.warn("Could not write Allure environment: {}", e.getMessage());
        }
    }
}
