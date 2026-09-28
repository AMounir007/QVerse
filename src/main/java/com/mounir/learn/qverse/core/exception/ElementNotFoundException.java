package com.mounir.learn.qverse.core.exception;

import com.mounir.learn.qverse.core.locator.Locator;

public class ElementNotFoundException extends QVerseException {
    public ElementNotFoundException(Locator locator, Throwable cause) {
        super("Element " + locator + " was not found or not ready (" + locator.toBy()
                + "). Check the locator, the page state, or add a fallback locator via Locator.orElse(...).", cause);
    }
}
