package com.mounir.learn.qverse.web;

import com.mounir.learn.qverse.config.ConfigManager;
import com.mounir.learn.qverse.core.driver.DriverManager;
import com.mounir.learn.qverse.core.element.ElementResolver;
import com.mounir.learn.qverse.core.exception.QVerseException;
import com.mounir.learn.qverse.core.locator.Locator;
import com.mounir.learn.qverse.core.retry.ExponentialBackoffRetry;
import com.mounir.learn.qverse.core.retry.RetryPolicy;
import com.mounir.learn.qverse.core.wait.Waits;
import com.mounir.learn.qverse.reporting.ReportManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.function.Supplier;

/**
 * <b>Facade pattern</b> - the single, business-level API for browser interaction.
 * <p>Every action automatically: waits with the right strategy, retries transient failures,
 * self-heals locators, logs, and appears as a readable Allure step. Methods return {@code this}
 * for fluent chaining.</p>
 */
public class WebActions {

    private final Supplier<WebDriver> driver;
    private final ElementResolver resolver;
    private final RetryPolicy retry;

    public WebActions() {
        this(DriverManager::web, ExponentialBackoffRetry.fromConfig());
    }

    public WebActions(Supplier<WebDriver> driver, RetryPolicy retry) {
        this.driver = driver;
        this.resolver = new ElementResolver(driver);
        this.retry = retry;
    }

    // ---------------- Navigation ----------------

    public WebActions open(String url) {
        ReportManager.step("Open " + url, () -> driver.get().get(url));
        return this;
    }

    // ---------------- Interactions ----------------

    public WebActions click(Locator locator) {
        perform("Click on " + locator, () -> resolver.resolve(locator, Waits.CLICKABLE).click());
        return this;
    }

    public WebActions doubleClick(Locator locator) {
        perform("Double-click on " + locator, () ->
                new Actions(driver.get()).doubleClick(resolver.resolve(locator, Waits.CLICKABLE)).perform());
        return this;
    }

    public WebActions type(Locator locator, String text) {
        perform("Type '" + text + "' into " + locator, () -> typeInto(locator, text));
        return this;
    }

    /** Same as type() but masks the value in logs and reports (passwords, tokens, PII). */
    public WebActions typeSecret(Locator locator, String secret) {
        perform("Type ****** into " + locator, () -> typeInto(locator, secret));
        return this;
    }

    public WebActions select(Locator locator, String visibleText) {
        perform("Select '" + visibleText + "' in " + locator, () ->
                new Select(resolver.resolve(locator, Waits.VISIBLE)).selectByVisibleText(visibleText));
        return this;
    }

    public WebActions hover(Locator locator) {
        perform("Hover over " + locator, () ->
                new Actions(driver.get()).moveToElement(resolver.resolve(locator, Waits.VISIBLE)).perform());
        return this;
    }

    public WebActions dragAndDrop(Locator source, Locator target) {
        perform("Drag " + source + " to " + target, () -> new Actions(driver.get())
                .dragAndDrop(resolver.resolve(source, Waits.VISIBLE), resolver.resolve(target, Waits.VISIBLE))
                .perform());
        return this;
    }

    public WebActions scroll(Locator locator) {
        perform("Scroll to " + locator, () -> js("arguments[0].scrollIntoView({block:'center'});",
                resolver.resolve(locator, Waits.PRESENT)));
        return this;
    }

    public WebActions scrollBy(int pixels) {
        perform("Scroll by " + pixels + "px", () -> js("window.scrollBy(0, arguments[0]);", pixels));
        return this;
    }

    public WebActions waitForElement(Locator locator) {
        ReportManager.step("Wait for " + locator, () -> resolver.resolve(locator, Waits.VISIBLE));
        return this;
    }

    /** Uploads a file given as a file-system path or a classpath resource (e.g. "files/invoice.pdf"). */
    public WebActions uploadFile(Locator fileInput, String file) {
        String absolute = resolveFile(file).toString();
        perform("Upload '" + file + "' via " + fileInput,
                () -> resolver.resolve(fileInput, Waits.PRESENT).sendKeys(absolute));
        return this;
    }

    // ---------------- Verifications ----------------

    public WebActions verifyText(Locator locator, String expected) {
        ReportManager.step("Verify " + locator + " shows '" + expected + "'", () -> {
            try {
                new WebDriverWait(driver.get(), timeout()).until(ExpectedConditions.textToBe(locator.toBy(), expected));
            } catch (TimeoutException ignored) {
                // fall through to a readable assertion message
            }
            Assert.assertEquals(getText(locator), expected, "Unexpected text in " + locator);
        });
        return this;
    }

    public WebActions verifyDisplayed(Locator locator) {
        ReportManager.step("Verify " + locator + " is displayed",
                () -> Assert.assertTrue(resolver.resolve(locator, Waits.VISIBLE).isDisplayed(), locator + " is not displayed"));
        return this;
    }

    public WebActions verifyNotDisplayed(Locator locator) {
        ReportManager.step("Verify " + locator + " is not displayed", () -> Assert.assertTrue(
                new WebDriverWait(driver.get(), timeout()).until(ExpectedConditions.invisibilityOfElementLocated(locator.toBy())),
                locator + " is still displayed"));
        return this;
    }

    public WebActions verifyUrlContains(String fragment) {
        ReportManager.step("Verify URL contains '" + fragment + "'", () -> {
            try {
                new WebDriverWait(driver.get(), timeout()).until(ExpectedConditions.urlContains(fragment));
            } catch (TimeoutException ignored) {
                // assertion below reports the actual URL
            }
            Assert.assertTrue(driver.get().getCurrentUrl().contains(fragment),
                    "URL '" + driver.get().getCurrentUrl() + "' does not contain '" + fragment + "'");
        });
        return this;
    }

    /** Waits until a file appears in the configured download directory. */
    public WebActions verifyFileDownloaded(String fileName) {
        ReportManager.step("Verify file '" + fileName + "' is downloaded", () -> {
            Path file = Path.of(ConfigManager.config().downloadDir(), fileName);
            try {
                new WebDriverWait(driver.get(), timeout()).until(d -> Files.exists(file));
            } catch (TimeoutException e) {
                Assert.fail("File was not downloaded: " + file.toAbsolutePath());
            }
        });
        return this;
    }

    // ---------------- Queries ----------------

    public String getText(Locator locator) {
        return retry.execute("Read text of " + locator, () -> resolver.resolve(locator, Waits.VISIBLE).getText().trim());
    }

    public boolean isDisplayed(Locator locator) {
        return resolver.isPresent(locator) && driver.get().findElement(locator.toBy()).isDisplayed();
    }

    // ---------------- Internals ----------------

    private void typeInto(Locator locator, String text) {
        WebElement el = resolver.resolve(locator, Waits.VISIBLE);
        el.clear();
        el.sendKeys(text);
    }

    private void perform(String description, Runnable action) {
        ReportManager.step(description, () -> retry.run(description, action));
    }

    private Object js(String script, Object... args) {
        return ((JavascriptExecutor) driver.get()).executeScript(script, args);
    }

    private static Duration timeout() {
        return Duration.ofSeconds(ConfigManager.config().explicitTimeout());
    }

    private static Path resolveFile(String file) {
        Path path = Path.of(file);
        if (Files.exists(path)) {
            return path.toAbsolutePath();
        }
        URL resource = Thread.currentThread().getContextClassLoader().getResource(file);
        if (resource == null) {
            throw new QVerseException("Upload file not found on disk or classpath: " + file);
        }
        try {
            return Path.of(resource.toURI());
        } catch (URISyntaxException e) {
            throw new QVerseException("Invalid upload file path: " + file, e);
        }
    }
}
