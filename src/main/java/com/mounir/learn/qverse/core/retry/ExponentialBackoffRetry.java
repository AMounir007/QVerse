package com.mounir.learn.qverse.core.retry;

import com.mounir.learn.qverse.config.ConfigManager;
import com.mounir.learn.qverse.core.logging.QVerseLogger;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;

import java.util.List;
import java.util.function.Supplier;

/**
 * Retries only TRANSIENT UI failures with exponential back-off. Genuine failures
 * (element truly missing, assertion errors) are never retried, so real bugs are not masked.
 */
public final class ExponentialBackoffRetry implements RetryPolicy {

    private static final QVerseLogger LOG = QVerseLogger.getLogger(ExponentialBackoffRetry.class);
    private static final List<Class<? extends WebDriverException>> RECOVERABLE = List.of(
            StaleElementReferenceException.class,
            ElementClickInterceptedException.class,
            ElementNotInteractableException.class,
            MoveTargetOutOfBoundsException.class);

    private final int maxAttempts;
    private final long initialDelayMillis;

    public ExponentialBackoffRetry(int maxAttempts, long initialDelayMillis) {
        this.maxAttempts = Math.max(1, maxAttempts);
        this.initialDelayMillis = initialDelayMillis;
    }

    public static ExponentialBackoffRetry fromConfig() {
        var c = ConfigManager.config();
        return new ExponentialBackoffRetry(c.actionRetryAttempts(), c.actionRetryDelayMillis());
    }

    @Override
    public <T> T execute(String actionName, Supplier<T> action) {
        for (int attempt = 1; ; attempt++) {
            try {
                return action.get();
            } catch (WebDriverException e) {
                if (!isRecoverable(e) || attempt >= maxAttempts) {
                    throw e;
                }
                long delay = initialDelayMillis * (1L << (attempt - 1));
                LOG.warn("Recovering '{}' after {} (attempt {}/{}), retrying in {} ms",
                        actionName, e.getClass().getSimpleName(), attempt, maxAttempts, delay);
                sleep(delay);
            }
        }
    }

    private static boolean isRecoverable(Throwable e) {
        return RECOVERABLE.stream().anyMatch(type -> type.isInstance(e));
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
