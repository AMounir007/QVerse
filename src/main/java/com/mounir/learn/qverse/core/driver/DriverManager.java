package com.mounir.learn.qverse.core.driver;

import com.mounir.learn.qverse.config.ConfigManager;
import com.mounir.learn.qverse.core.exception.DriverInitializationException;
import com.mounir.learn.qverse.core.logging.QVerseLogger;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

/**
 * Thread-safe session registry. Each test thread owns its own browser / device session via ThreadLocal,
 * which is the foundation of safe parallel execution.
 */
public final class DriverManager {

    private static final QVerseLogger LOG = QVerseLogger.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> WEB = new ThreadLocal<>();
    private static final ThreadLocal<AppiumDriver> MOBILE = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void startWeb() {
        if (WEB.get() == null) {
            var browser = ConfigManager.config().browser();
            LOG.info("Starting {} browser session", browser);
            WEB.set(WebDriverFactory.create(browser));
        }
    }

    public static void startMobile() {
        if (MOBILE.get() == null) {
            LOG.info("Starting {} mobile session", ConfigManager.config().mobilePlatform());
            MOBILE.set(MobileDriverFactory.create());
        }
    }

    public static WebDriver web() {
        WebDriver driver = WEB.get();
        if (driver == null) {
            throw new DriverInitializationException(
                    "No browser session on this thread. Extend WebTest or call DriverManager.startWeb().");
        }
        return driver;
    }

    public static AppiumDriver mobile() {
        AppiumDriver driver = MOBILE.get();
        if (driver == null) {
            throw new DriverInitializationException(
                    "No mobile session on this thread. Extend MobileTest or call DriverManager.startMobile().");
        }
        return driver;
    }

    /** The session currently in use (mobile preferred), used for screenshots. */
    public static Optional<WebDriver> activeDriver() {
        return Optional.<WebDriver>ofNullable(MOBILE.get()).or(() -> Optional.ofNullable(WEB.get()));
    }

    public static void quitAll() {
        quit(WEB);
        quit(MOBILE);
    }

    private static void quit(ThreadLocal<? extends WebDriver> holder) {
        WebDriver driver = holder.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                LOG.warn("Ignoring error while closing session: {}", e.getMessage());
            } finally {
                holder.remove();
            }
        }
    }
}
