package com.mounir.learn.qverse.core.element;

import com.mounir.learn.qverse.config.ConfigManager;
import com.mounir.learn.qverse.config.QVerseConfig;
import com.mounir.learn.qverse.core.ai.AiExtensions;
import com.mounir.learn.qverse.core.ai.LocatorHealer;
import com.mounir.learn.qverse.core.exception.ElementNotFoundException;
import com.mounir.learn.qverse.core.locator.Locator;
import com.mounir.learn.qverse.core.logging.QVerseLogger;
import com.mounir.learn.qverse.core.wait.WaitStrategy;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Central element synchronisation engine shared by Web and Mobile (AppiumDriver is a WebDriver).
 * Fluent wait (ignores NoSuchElement / Stale) -> on timeout, asks self-healers -> clear business error.
 */
public final class ElementResolver {

    private static final QVerseLogger LOG = QVerseLogger.getLogger(ElementResolver.class);
    private final Supplier<? extends WebDriver> driver;

    public ElementResolver(Supplier<? extends WebDriver> driver) {
        this.driver = driver;
    }

    public WebElement resolve(Locator locator, WaitStrategy strategy) {
        QVerseConfig c = ConfigManager.config();
        try {
            return waitFor(locator.toBy(), strategy, Duration.ofSeconds(c.explicitTimeout()));
        } catch (TimeoutException e) {
            return heal(locator, strategy, c).orElseThrow(() -> new ElementNotFoundException(locator, e));
        }
    }

    public boolean isPresent(Locator locator) {
        return !driver.get().findElements(locator.toBy()).isEmpty();
    }

    private WebElement waitFor(By by, WaitStrategy strategy, Duration timeout) {
        return new FluentWait<WebDriver>(driver.get())
                .withTimeout(timeout)
                .pollingEvery(Duration.ofMillis(ConfigManager.config().pollingMillis()))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .until(strategy.condition(by));
    }

    private Optional<WebElement> heal(Locator locator, WaitStrategy strategy, QVerseConfig c) {
        if (!c.selfHealingEnabled()) {
            return Optional.empty();
        }
        for (LocatorHealer healer : AiExtensions.healers()) {
            Optional<By> healed = healer.heal(driver.get(), locator);
            if (healed.isPresent()) {
                LOG.warn("SELF-HEALED {}: {} -> {} (please update the primary locator)",
                        locator, locator.toBy(), healed.get());
                try {
                    return Optional.of(waitFor(healed.get(), strategy, Duration.ofSeconds(3)));
                } catch (TimeoutException ignored) {
                    // try next healer
                }
            }
        }
        return Optional.empty();
    }
}
