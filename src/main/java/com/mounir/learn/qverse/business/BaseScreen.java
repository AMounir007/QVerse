package com.mounir.learn.qverse.business;

import com.mounir.learn.qverse.core.locator.Locator;
import com.mounir.learn.qverse.mobile.MobileActions;

/** <b>Template Method</b> base for mobile screens (same contract as {@link BasePage}). */
public abstract class BaseScreen<T extends BaseScreen<T>> {

    protected final MobileActions mobile = new MobileActions();

    protected abstract Locator screenIdentity();

    public T verifyLoaded() {
        mobile.verifyElement(screenIdentity());
        return self();
    }

    @SuppressWarnings("unchecked")
    protected final T self() {
        return (T) this;
    }
}
