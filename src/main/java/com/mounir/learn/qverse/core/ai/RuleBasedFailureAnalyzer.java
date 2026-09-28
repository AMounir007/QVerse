package com.mounir.learn.qverse.core.ai;

import com.mounir.learn.qverse.core.exception.DriverInitializationException;
import com.mounir.learn.qverse.core.exception.ElementNotFoundException;
import com.mounir.learn.qverse.core.exception.TestDataException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Deterministic, offline failure classification - the baseline every AI analyzer must beat.
 * <p>
 * Decisions:
 * <ul>
 *   <li>The WHOLE cause chain is inspected (not only the root cause): Selenium's TimeoutException
 *       usually wraps a NoSuchElementException, so root-only checks misclassify it.</li>
 *   <li>Third-party exceptions are matched by class name so the analyzer stays driver-agnostic
 *       (Selenium, Appium, Playwright...) and has no compile-time dependency on any driver library.</li>
 *   <li>Null-safe and cycle-safe: it must never throw inside a listener.</li>
 * </ul>
 */
public final class RuleBasedFailureAnalyzer implements AiFailureAnalyzer {

    private static final Set<String> SYNC_EXCEPTIONS = Set.of(
            "TimeoutException", "StaleElementReferenceException",
            "ElementClickInterceptedException", "ElementNotInteractableException");
    private static final Set<String> ENVIRONMENT_EXCEPTIONS = Set.of(
            "SessionNotCreatedException", "NoSuchSessionException", "UnreachableBrowserException");

    @Override
    public FailureInsight analyze(Throwable failure, String executionLog) {
        if (failure == null) {
            return new FailureInsight("UNKNOWN", "No exception was reported",
                    "Inspect the attached log and screenshot.");
        }
        List<Throwable> chain = causeChain(failure);
        Throwable root = chain.getLast();

        if (has(chain, ElementNotFoundException.class)) {
            return new FailureInsight("LOCATOR", message(find(chain, ElementNotFoundException.class)),
                    "UI probably changed. Update the locator or add a fallback with Locator.orElse().");
        }
        if (has(chain, TestDataException.class)) {
            return new FailureInsight("TEST_DATA", message(find(chain, TestDataException.class)),
                    "Check the data file path and format.");
        }
        if (has(chain, DriverInitializationException.class) || hasName(chain, ENVIRONMENT_EXCEPTIONS)
                || has(chain, IOException.class)) {
            return new FailureInsight("ENVIRONMENT", message(root),
                    "Infrastructure issue (browser, grid, Appium, network). Not a product defect.");
        }
        if (hasName(chain, SYNC_EXCEPTIONS)) {
            return new FailureInsight("SYNCHRONIZATION", root.getClass().getSimpleName() + ": " + message(root),
                    "Page was slow or re-rendered. Increase explicit.timeout.seconds or verify the page state.");
        }
        if (failure instanceof AssertionError) {
            String msg = message(failure);
            boolean api = msg.contains("status code") || msg.contains("schema");
            return new FailureInsight(api ? "API_CONTRACT" : "ASSERTION", msg,
                    "Business expectation not met - probable product defect. Review expected vs actual values.");
        }
        return new FailureInsight("UNKNOWN", failure.getClass().getSimpleName() + ": " + message(failure),
                "Inspect the attached log and screenshot.");
    }

    /** Failure first, root cause last; stops on cycles. */
    private static List<Throwable> causeChain(Throwable failure) {
        List<Throwable> chain = new ArrayList<>();
        Set<Throwable> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Throwable t = failure; t != null && seen.add(t); t = t.getCause()) {
            chain.add(t);
        }
        return chain;
    }

    private static boolean has(List<Throwable> chain, Class<? extends Throwable> type) {
        return find(chain, type) != null;
    }

    private static Throwable find(List<Throwable> chain, Class<? extends Throwable> type) {
        return chain.stream().filter(type::isInstance).findFirst().orElse(null);
    }

    private static boolean hasName(List<Throwable> chain, Set<String> simpleNames) {
        return chain.stream().anyMatch(t -> simpleNames.contains(t.getClass().getSimpleName()));
    }

    private static String message(Throwable t) {
        return Objects.toString(t.getMessage(), t.getClass().getSimpleName());
    }
}
