package com.mounir.learn.qverse.reporting;

import com.mounir.learn.qverse.core.driver.DriverManager;
import com.mounir.learn.qverse.core.logging.QVerseLogger;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

/**
 * <b>Facade pattern</b> over Allure + Log4J2. Framework code reports through this single API, so the
 * reporting engine could be replaced (ReportPortal, Extent, custom dashboard) in one class.
 */
public final class ReportManager {

    private static final QVerseLogger LOG = QVerseLogger.getLogger(ReportManager.class);

    private ReportManager() {
    }

    /** Runs an action as a named, business-readable report step. */
    public static void step(String name, Runnable action) {
        LOG.info("STEP: {}", name);
        Allure.ThrowableRunnableVoid runnable = action::run;
        Allure.step(name, runnable);
    }

    /** Runs an action as a report step and returns its result. */
    public static <T> T stepAndReturn(String name, Supplier<T> action) {
        LOG.info("STEP: {}", name);
        Allure.ThrowableRunnable<T> runnable = action::get;
        return Allure.step(name, runnable);
    }

    public static void info(String message) {
        LOG.info(message);
        Allure.step(message);
    }

    public static void attachScreenshot(String name) {
        DriverManager.activeDriver().ifPresent(driver -> {
            try {
                byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment(name, "image/png", new ByteArrayInputStream(png), "png");
            } catch (Exception e) {
                LOG.warn("Screenshot could not be captured: {}", e.getMessage());
            }
        });
    }

    public static void attachText(String name, String content) {
        Allure.addAttachment(name, "text/plain", content == null ? "" : content, "txt");
    }

    public static void attachJson(String name, String json) {
        Allure.addAttachment(name, "application/json",
                new ByteArrayInputStream(String.valueOf(json).getBytes(StandardCharsets.UTF_8)), "json");
    }
}
