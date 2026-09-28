# QVerse — Developer Guide (framework & DSL engineers)

## Build & run
```bash
mvn clean test                      # regression (web + api), qa env
mvn test -Psmoke,staging,headless   # smoke on staging, headless
mvn test -Papi                      # API only (fast, 8 threads)
mvn test -Pweb -Dbrowser=FIREFOX
mvn test -Pgrid                     # needs docker-compose.grid.yml up
mvn test -Pmobile                   # needs Appium + device
mvn allure:serve                    # open the Allure report
```
Dashboard: `target/qverse-dashboard/index.html` · machine summary: `summary.json` · logs: `target/logs/`.

## Adding a page (Web DSL)
```java
public final class InvoicePage extends BasePage<InvoicePage> {
    private static final Locator AMOUNT = Locator.id("Invoice amount", "amount");
    private static final Locator ROW = Locator.xpath("Invoice row %s", "//tr[td='%s']"); // dynamic

    public static InvoicePage open() { return new InvoicePage().load(); }
    @Override protected String path() { return "/invoices"; }
    @Override protected Locator pageIdentity() { return AMOUNT; }

    public InvoicePage enterAmount(String v) { web.type(AMOUNT, v); return this; }
    public InvoicePage verifyInvoiceListed(String no) { web.verifyDisplayed(ROW.with(no)); return this; }
}
```
Rules: locators are `private static final`; public methods are business verbs returning a page (fluent).

## Adding an API service
```java
public final class PolicyAPI extends BaseApi {
    @Override protected Map<String, String> defaultHeaders() {
        return Map.of("Authorization", "Bearer " + System.getenv("POLICY_TOKEN"));
    }
    public static ApiActions issuePolicy(Policy p) {
        PolicyAPI s = new PolicyAPI();
        return s.api().post(s.request("/policies").body(p).build()).verifyStatusCode(201);
    }
}
```

## Adding a mobile screen
Extend `BaseScreen<T>`, prefer `Locator.accessibilityId(...)` (cross-platform), use `mobile.tap/sendKeys/swipe/scroll`.

## Extension points
| Need | How |
|---|---|
| New browser / cloud vendor | extend `WebDriverFactory.options()/remote()` |
| New wait condition | implement `WaitStrategy` |
| Different retry behaviour | implement `RetryPolicy`, pass to `new WebActions(driver, policy)` |
| New data source (DB, CSV) | implement `TestDataReader`, `TestDataFactory.register(...)` |
| AI healer / analyzer / generator | implement SPI + `META-INF/services` file |
| New report sink | extend `ReportManager` (single facade) |

## Configuration keys
See `src/main/resources/config/qverse.properties` (defaults) and `QVerseConfig` (typed contract).
Priority: `-Dkey` > env var > `env/<env>.properties` > defaults.

## Thread-safety contract
* Never store drivers, responses or test data in `static` mutable fields — use `DriverManager`, `QVerseContext`.
* `WebActions`/`ApiActions`/`MobileActions` instances are cheap; create per page/step.
* `ApiClient` and `ConfigManager` are the only shared singletons and are immutable/stateless.
