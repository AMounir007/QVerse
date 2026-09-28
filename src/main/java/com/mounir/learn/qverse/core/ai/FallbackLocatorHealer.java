package com.mounir.learn.qverse.core.ai;

import com.mounir.learn.qverse.core.locator.Locator;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;

import java.util.Optional;

/** Default healer: tries the fallback locators declared with {@link Locator#orElse(Locator)}. */
public final class FallbackLocatorHealer implements LocatorHealer {

    @Override
    public Optional<By> heal(SearchContext context, Locator brokenLocator) {
        return brokenLocator.fallbacks().stream()
                .map(Locator::toBy)
                .filter(by -> !context.findElements(by).isEmpty())
                .findFirst();
    }
}
