package com.mounir.learn.qverse.business;

import com.mounir.learn.qverse.config.ConfigManager;
import com.mounir.learn.qverse.core.locator.Locator;
import com.mounir.learn.qverse.web.WebActions;

/**
 * <b>Template Method pattern</b> for web pages. {@link #load()} fixes the algorithm
 * (before-load hook -> navigate -> verify identity), subclasses only declare WHAT the page is.
 * The self-type {@code T} lets every business step return the concrete page for fluent DSL chains.
 */
public abstract class BasePage<T extends BasePage<T>> {

    protected final WebActions web = new WebActions();

    /** Relative path of the page, appended to base.url. */
    protected abstract String path();

    /** An element that proves the page is loaded (header, form, unique button...). */
    protected abstract Locator pageIdentity();

    /** Optional hook, e.g. clearing cookies or seeding data. */
    protected void beforeLoad() {
    }

    protected final T load() {
        beforeLoad();
        web.open(ConfigManager.config().baseUrl() + path());
        return verifyLoaded();
    }

    public T verifyLoaded() {
        web.verifyDisplayed(pageIdentity());
        return self();
    }

    @SuppressWarnings("unchecked")
    protected final T self() {
        return (T) this;
    }
}
