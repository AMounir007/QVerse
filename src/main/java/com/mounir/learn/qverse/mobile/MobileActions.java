package com.mounir.learn.qverse.mobile;

import com.mounir.learn.qverse.core.driver.DriverManager;
import com.mounir.learn.qverse.core.element.ElementResolver;
import com.mounir.learn.qverse.core.locator.Locator;
import com.mounir.learn.qverse.core.retry.ExponentialBackoffRetry;
import com.mounir.learn.qverse.core.retry.RetryPolicy;
import com.mounir.learn.qverse.core.wait.Waits;
import com.mounir.learn.qverse.reporting.ReportManager;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

/**
 * <b>Facade</b> for native/hybrid mobile apps. Uses W3C Actions (the modern, cross-platform gesture API)
 * so the same code works on Android (UiAutomator2) and iOS (XCUITest).
 */
public class MobileActions {

    private static final Duration GESTURE = Duration.ofMillis(600);
    private final Supplier<AppiumDriver> driver;
    private final ElementResolver resolver;
    private final RetryPolicy retry;

    public MobileActions() {
        this(DriverManager::mobile, ExponentialBackoffRetry.fromConfig());
    }

    public MobileActions(Supplier<AppiumDriver> driver, RetryPolicy retry) {
        this.driver = driver;
        this.resolver = new ElementResolver(driver);
        this.retry = retry;
    }

    public MobileActions tap(Locator locator) {
        perform("Tap " + locator, () -> resolver.resolve(locator, Waits.CLICKABLE).click());
        return this;
    }

    public MobileActions sendKeys(Locator locator, String text) {
        perform("Enter '" + text + "' into " + locator, () -> {
            WebElement el = resolver.resolve(locator, Waits.VISIBLE);
            el.clear();
            el.sendKeys(text);
        });
        return this;
    }

    public MobileActions swipe(Direction direction) {
        perform("Swipe " + direction, () -> {
            Dimension size = driver.get().manage().window().getSize();
            int cx = size.width / 2;
            int cy = size.height / 2;
            int dx = (int) (size.width * 0.35);
            int dy = (int) (size.height * 0.35);
            Point from;
            Point to;
            switch (direction) {
                case UP -> { from = new Point(cx, cy + dy); to = new Point(cx, cy - dy); }
                case DOWN -> { from = new Point(cx, cy - dy); to = new Point(cx, cy + dy); }
                case LEFT -> { from = new Point(cx + dx, cy); to = new Point(cx - dx, cy); }
                default -> { from = new Point(cx - dx, cy); to = new Point(cx + dx, cy); }
            }
            driver.get().perform(List.of(line("finger", from, to)));
        });
        return this;
    }

    /** Scrolls (swipes) in the given direction until the element is present, up to maxSwipes. */
    public MobileActions scroll(Locator target, Direction direction, int maxSwipes) {
        ReportManager.step("Scroll " + direction + " to " + target, () -> {
            for (int i = 0; i < maxSwipes && !resolver.isPresent(target); i++) {
                swipe(direction);
            }
            resolver.resolve(target, Waits.VISIBLE);
        });
        return this;
    }

    public MobileActions scroll(Direction direction) {
        return swipe(direction);
    }

    /** Two-finger pinch (zoom out) centred on the element. */
    public MobileActions pinch(Locator locator) {
        perform("Pinch on " + locator, () -> twoFingerGesture(resolver.resolve(locator, Waits.VISIBLE), true));
        return this;
    }

    /** Two-finger spread (zoom in) centred on the element. */
    public MobileActions zoom(Locator locator) {
        perform("Zoom on " + locator, () -> twoFingerGesture(resolver.resolve(locator, Waits.VISIBLE), false));
        return this;
    }

    public MobileActions verifyElement(Locator locator) {
        ReportManager.step("Verify " + locator + " is displayed", () ->
                Assert.assertTrue(resolver.resolve(locator, Waits.VISIBLE).isDisplayed(), locator + " is not displayed"));
        return this;
    }

    public MobileActions verifyText(Locator locator, String expected) {
        ReportManager.step("Verify " + locator + " shows '" + expected + "'", () ->
                Assert.assertEquals(getText(locator), expected, "Unexpected text in " + locator));
        return this;
    }

    public String getText(Locator locator) {
        return retry.execute("Read " + locator, () -> resolver.resolve(locator, Waits.VISIBLE).getText().trim());
    }

    public MobileActions hideKeyboard() {
        ReportManager.step("Hide keyboard", () -> {
            try {
                driver.get().executeScript("mobile: hideKeyboard");
            } catch (Exception ignored) {
                // keyboard not shown
            }
        });
        return this;
    }

    // ---------------- Internals ----------------

    private void twoFingerGesture(WebElement element, boolean pinch) {
        Rectangle r = element.getRect();
        int cx = r.getX() + r.getWidth() / 2;
        int cy = r.getY() + r.getHeight() / 2;
        int offset = Math.max(40, (int) (Math.min(r.getWidth(), r.getHeight()) * 0.4));
        Point centerA = new Point(cx, cy - 10);
        Point centerB = new Point(cx, cy + 10);
        Point farA = new Point(cx, cy - offset);
        Point farB = new Point(cx, cy + offset);
        Sequence a = pinch ? line("finger1", farA, centerA) : line("finger1", centerA, farA);
        Sequence b = pinch ? line("finger2", farB, centerB) : line("finger2", centerB, farB);
        driver.get().perform(List.of(a, b));
    }

    private static Sequence line(String name, Point from, Point to) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, name);
        Sequence s = new Sequence(finger, 0);
        s.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), from.getX(), from.getY()));
        s.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        s.addAction(finger.createPointerMove(GESTURE, PointerInput.Origin.viewport(), to.getX(), to.getY()));
        s.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        return s;
    }

    private void perform(String description, Runnable action) {
        ReportManager.step(description, () -> retry.run(description, action));
    }
}
