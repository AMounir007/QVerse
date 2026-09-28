package com.mounir.learn.qverse.config;

import org.aeonbits.owner.Config;

/**
 * Typed configuration contract (Owner library).
 * <p>
 * Decision: configuration is resolved in priority order so that the SAME build can run
 * anywhere (laptop, CI, grid, cloud) without code changes:
 * <ol>
 *   <li>JVM system properties (-Dbrowser=FIREFOX)</li>
 *   <li>OS environment variables (secrets in CI)</li>
 *   <li>Environment file: env/${env}.properties</li>
 *   <li>Framework defaults: config/qverse.properties</li>
 * </ol>
 */
@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:env/${env}.properties",
        "classpath:config/qverse.properties"
})
public interface QVerseConfig extends Config {

    // ---------- Environment ----------
    @Key("env") @DefaultValue("qa")
    String env();

    @Key("base.url")
    String baseUrl();

    @Key("api.base.url")
    String apiBaseUrl();

    // ---------- Web ----------
    @Key("browser") @DefaultValue("CHROME")
    BrowserType browser();

    @Key("headless") @DefaultValue("false")
    boolean headless();

    @Key("execution.mode") @DefaultValue("LOCAL")
    ExecutionMode executionMode();

    @Key("grid.url") @DefaultValue("http://localhost:4444/wd/hub")
    String gridUrl();

    @Key("download.dir") @DefaultValue("target/downloads")
    String downloadDir();

    @Key("page.load.timeout.seconds") @DefaultValue("60")
    int pageLoadTimeout();

    // ---------- Synchronisation & resilience ----------
    @Key("explicit.timeout.seconds") @DefaultValue("15")
    int explicitTimeout();

    @Key("polling.interval.millis") @DefaultValue("250")
    long pollingMillis();

    @Key("action.retry.attempts") @DefaultValue("3")
    int actionRetryAttempts();

    @Key("action.retry.delay.millis") @DefaultValue("300")
    long actionRetryDelayMillis();

    @Key("test.retry.count") @DefaultValue("1")
    int testRetryCount();

    // ---------- Mobile ----------
    @Key("appium.url") @DefaultValue("http://127.0.0.1:4723")
    String appiumUrl();

    @Key("mobile.platform") @DefaultValue("ANDROID")
    String mobilePlatform();

    @Key("mobile.device") @DefaultValue("emulator-5554")
    String mobileDevice();

    @Key("mobile.platform.version") @DefaultValue("")
    String mobilePlatformVersion();

    @Key("mobile.app") @DefaultValue("")
    String mobileApp();

    // ---------- AI ----------
    @Key("ai.self.healing.enabled") @DefaultValue("true")
    boolean selfHealingEnabled();

    @Key("ai.failure.analysis.enabled") @DefaultValue("true")
    boolean failureAnalysisEnabled();
}
