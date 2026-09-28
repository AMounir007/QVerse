package com.mounir.learn.qverse.core.wait;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * <b>Strategy pattern</b> - decides WHEN an element is "ready" for an action.
 * Actions pick the right strategy (click -> CLICKABLE, upload -> PRESENT) so testers never write waits.
 */
public interface WaitStrategy {

    String name();

    ExpectedCondition<WebElement> condition(By by);
}
