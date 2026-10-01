# QVerse — Enterprise Automation Ecosystem

Domain-independent, AI-ready automation for **Web · API · Mobile** on Java 21, TestNG, Selenium, REST Assured, Appium and Allure.

```java
LoginPage.open().enterUsername("admin").enterPassword("password").clickLogin();
CustomerAPI.createCustomer().shouldBeCreated();
TransferScreen.open().transferMoney().verifyTransferSuccessful();
```

Testers write business actions; QVerse handles waits, retries, stale elements, dynamic locators, self-healing,
sessions, parallel execution, environments, test data, screenshots, logs, API traffic capture, reporting and failure analysis.

## Quick start
```bash
mvn clean test            # web + api regression (qa)
mvn test -Papi            # API suite
mvn test -Psmoke,headless # smoke gate
mvn allure:serve          # Allure report
```
Execution dashboard: `target/qverse-dashboard/index.html`

## Documentation
| Guide | For |
|---|---|
| [Architecture Guide](docs/ARCHITECTURE_GUIDE.md) | diagram, layers, folder/package structure, patterns, decisions |
| [Developer Guide](docs/DEVELOPER_GUIDE.md) | extending the framework and DSL |
| [Tester Guide](docs/TESTER_GUIDE.md) | writing business tests |
| [Onboarding Guide](docs/ONBOARDING_GUIDE.md) | first 3 days |
| [Best Practices](docs/BEST_PRACTICES_GUIDE.md) | standards & governance |

## Maven profiles
`qa` `staging` `prod` · `smoke` `regression` `web` `api` `mobile` · `headless` `grid` `cloud` `ci`

## Use QVerse in your project (JitPack)

[![](https://jitpack.io/v/AMounir007/QVerse.svg)](https://jitpack.io/#AMounir007/QVerse)

Add to your project's `pom.xml`:
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.AMounir007</groupId>
    <artifactId>QVerse</artifactId>
    <version>1.0.1</version>
  </dependency>
</dependencies>
```
Then run `mvn clean install -U`.

> Use `1.0.1` or later — `1.0.0` failed to build on JitPack (Maven 3.6.3 requirement) and is not usable.

### Releasing a new version
1. Commit & push changes to `main`.
2. Create a tag: `git tag 1.0.2` then `git push origin 1.0.2`.
3. On GitHub → **Releases → Draft a new release**, pick the tag, paste notes from [CHANGELOG.md](CHANGELOG.md).
4. Open https://jitpack.io/#AMounir007/QVerse → click **Get it** next to the version to trigger the build.

## Release notes
- Full history: [CHANGELOG.md](CHANGELOG.md)
- Latest: [QVerse 1.0.1](docs/RELEASE_NOTES_1.0.1.md) — ✅ available on JitPack
