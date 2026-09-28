package com.mounir.learn.qverse.core.retry;

import java.util.function.Supplier;

/**
 * <b>Strategy pattern</b> for action-level failure recovery (stale elements, overlays, animations).
 * Swappable: e.g. a cloud-tuned policy with longer back-off, or a no-retry policy for debugging.
 */
public interface RetryPolicy {

    <T> T execute(String actionName, Supplier<T> action);

    default void run(String actionName, Runnable action) {
        execute(actionName, () -> {
            action.run();
            return null;
        });
    }
}
