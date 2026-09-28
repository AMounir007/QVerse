package com.mounir.learn.qverse.testng;

import com.mounir.learn.qverse.config.ConfigManager;
import com.mounir.learn.qverse.core.logging.QVerseLogger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Re-runs failed tests up to {@code test.retry.count} times to absorb infrastructure flakiness.
 * Retries are reported as RETRIED on the dashboard so flaky tests stay visible, not hidden.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final QVerseLogger LOG = QVerseLogger.getLogger(RetryAnalyzer.class);
    private final AtomicInteger attempts = new AtomicInteger();

    @Override
    public boolean retry(ITestResult result) {
        int max = ConfigManager.config().testRetryCount();
        if (attempts.incrementAndGet() <= max) {
            LOG.warn("Retrying {} (attempt {}/{})", result.getName(), attempts.get(), max);
            return true;
        }
        return false;
    }
}
