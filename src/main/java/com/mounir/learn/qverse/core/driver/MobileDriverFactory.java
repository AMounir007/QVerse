package com.mounir.learn.qverse.core.driver;

import com.mounir.learn.qverse.config.ConfigManager;
import com.mounir.learn.qverse.config.QVerseConfig;
import com.mounir.learn.qverse.core.exception.DriverInitializationException;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.net.URL;
import java.net.URI;
import java.time.Duration;

/**
 * <b>Factory pattern</b> for Appium sessions (Android / iOS). Tests never touch capabilities;
 * everything comes from configuration, so the same test runs on emulator, real device or device cloud.
 */
public final class MobileDriverFactory {

    private MobileDriverFactory() {
    }

    public static AppiumDriver create() {
        QVerseConfig c = ConfigManager.config();
        try {
            URL server = URI.create(c.appiumUrl()).toURL();
            if ("IOS".equalsIgnoreCase(c.mobilePlatform())) {
                XCUITestOptions o = new XCUITestOptions()
                        .setDeviceName(c.mobileDevice())
                        .setNewCommandTimeout(Duration.ofSeconds(120));
                if (!c.mobilePlatformVersion().isBlank()) o.setPlatformVersion(c.mobilePlatformVersion());
                if (!c.mobileApp().isBlank()) o.setApp(c.mobileApp());
                return new IOSDriver(server, o);
            }
            UiAutomator2Options o = new UiAutomator2Options()
                    .setDeviceName(c.mobileDevice())
                    .setNewCommandTimeout(Duration.ofSeconds(120))
                    .setAutoGrantPermissions(true);
            if (!c.mobilePlatformVersion().isBlank()) o.setPlatformVersion(c.mobilePlatformVersion());
            if (!c.mobileApp().isBlank()) o.setApp(c.mobileApp());
            return new AndroidDriver(server, o);
        } catch (Exception e) {
            throw new DriverInitializationException("Unable to start a " + c.mobilePlatform()
                    + " session on " + c.appiumUrl() + ". Is the Appium server running and the device connected?", e);
        }
    }
}
