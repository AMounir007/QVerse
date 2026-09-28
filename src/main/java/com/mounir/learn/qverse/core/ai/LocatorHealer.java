package com.mounir.learn.qverse.core.ai;

import com.mounir.learn.qverse.core.locator.Locator;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;

import java.util.Optional;

/**
 * AI-ready SPI for <b>self-healing locators</b>. The default implementation uses declared fallbacks;
 * an ML/LLM implementation (DOM similarity, visual matching) can be plugged in through
 * {@code META-INF/services/com.mounir.learn.qverse.core.ai.LocatorHealer} without touching the framework.
 */
public interface LocatorHealer {

    Optional<By> heal(SearchContext context, Locator brokenLocator);
}
