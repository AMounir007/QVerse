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

```xml
<repositories>
  <repository><id>jitpack.io</id><url>https://jitpack.io</url></repository>
</repositories>
<dependency>
  <groupId>com.github.AMounir007</groupId>
  <artifactId>QVerse</artifactId>
  <version>1.0.0</version>
</dependency>
```
