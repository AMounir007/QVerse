package com.mounir.learn.qverse.core.wait;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Built-in wait strategies. Custom strategies can implement {@link WaitStrategy} directly. */
public enum Waits implements WaitStrategy {
    PRESENT {
        @Override
        public ExpectedCondition<WebElement> condition(By by) {
            return ExpectedConditions.presenceOfElementLocated(by);
        }
    },
    VISIBLE {
        @Override
        public ExpectedCondition<WebElement> condition(By by) {
            return ExpectedConditions.visibilityOfElementLocated(by);
        }
    },
    CLICKABLE {
        @Override
        public ExpectedCondition<WebElement> condition(By by) {
            return ExpectedConditions.elementToBeClickable(by);
        }
    }
}
