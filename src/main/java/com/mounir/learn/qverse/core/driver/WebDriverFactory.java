package com.mounir.learn.qverse.core.driver;

import com.mounir.learn.qverse.config.BrowserType;
import com.mounir.learn.qverse.config.ConfigManager;
import com.mounir.learn.qverse.config.ExecutionMode;
import com.mounir.learn.qverse.config.QVerseConfig;
import com.mounir.learn.qverse.core.exception.DriverInitializationException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

/**
 * <b>Factory pattern</b> - the only place that knows HOW a browser is created
 * (local via WebDriverManager, Selenium Grid, or cloud). Adding a browser/provider = one change here.
 */
public final class WebDriverFactory {

    private WebDriverFactory() {
    }

    public static WebDriver create(BrowserType browser) {
        QVerseConfig c = ConfigManager.config();
        try {
            Capabilities options = options(browser, c);
            WebDriver driver = c.executionMode() == ExecutionMode.LOCAL
                    ? local(browser, options)
                    : remote(c.gridUrl(), options);
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(c.pageLoadTimeout()));
            if (!c.headless()) {
                driver.manage().window().maximize();
            }
            return driver;
        } catch (Exception e) {
            throw new DriverInitializationException("Unable to start " + browser + " in " + c.executionMode()
                    + " mode. Verify the browser is installed or the grid/cloud URL is reachable.", e);
        }
    }

    private static WebDriver local(BrowserType browser, Capabilities options) {
        return switch (browser) {
            case CHROME -> {
                WebDriverManager.chromedriver().setup();
                yield new ChromeDriver((ChromeOptions) options);
            }
            case FIREFOX -> {
                WebDriverManager.firefoxdriver().setup();
                yield new FirefoxDriver((FirefoxOptions) options);
            }
            case EDGE -> {
                WebDriverManager.edgedriver().setup();
                yield new EdgeDriver((EdgeOptions) options);
            }
            case SAFARI -> new SafariDriver((SafariOptions) options);
        };
    }

    private static WebDriver remote(String url, Capabilities options) throws Exception {
        RemoteWebDriver driver = new RemoteWebDriver(URI.create(url).toURL(), options);
        driver.setFileDetector(new LocalFileDetector()); // enables uploads from the test machine
        return driver;
    }

    private static Capabilities options(BrowserType browser, QVerseConfig c) {
        String downloads = Path.of(c.downloadDir()).toAbsolutePath().toString();
        return switch (browser) {
            case CHROME -> {
                ChromeOptions o = new ChromeOptions();
                if (c.headless()) o.addArguments("--headless=new");
                o.addArguments("--window-size=1920,1080", "--disable-notifications", "--remote-allow-origins=*");
                o.setExperimentalOption("prefs", Map.of(
                        "download.default_directory", downloads,
                        "download.prompt_for_download", false,
                        "credentials_enable_service", false,
                        "profile.password_manager_enabled", false,
                        "profile.password_manager_leak_detection", false));
                yield o;
            }
            case FIREFOX -> {
                FirefoxOptions o = new FirefoxOptions();
                if (c.headless()) o.addArguments("-headless");
                o.addPreference("browser.download.folderList", 2);
                o.addPreference("browser.download.dir", downloads);
                yield o;
            }
            case EDGE -> {
                EdgeOptions o = new EdgeOptions();
                if (c.headless()) o.addArguments("--headless=new");
                o.addArguments("--window-size=1920,1080");
                yield o;
            }
            case SAFARI -> new SafariOptions();
        };
    }
}
