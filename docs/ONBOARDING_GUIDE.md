# QVerse — Framework Onboarding Guide

## Day 0 — setup (30 min)
1. Install **JDK 21**, **Maven 3.9+**, Chrome, an IDE (IntelliJ + Lombok plugin enabled, annotation processing ON).
2. Optional: Allure CLI, Docker (Selenium Grid), Node + Appium 2 (`npm i -g appium && appium driver install uiautomator2`).
3. `git clone` → `mvn -q test-compile` (downloads everything).
4. `mvn test -Papi` → then `mvn allure:serve` to see your first report.

## Day 1 — understand
* Read `docs/ARCHITECTURE_GUIDE.md` (diagram + layers, 15 min).
* Open the examples: `examples/web/WebLoginTest`, `examples/api/CustomerApiTest`, `examples/mobile/MobileTransferTest`.
* Run `WebLoginTest` from the IDE and open `target/qverse-dashboard/index.html`.

## Day 2 — first test for YOUR application
1. Point the environment: edit `src/test/resources/env/qa.properties` (`base.url`, `api.base.url`).
2. Create a package per domain: `com.<company>.<product>.pages|screens|api|tests`.
3. Write one page object (copy `LoginPage`) and one test extending `WebTest`.
4. Tag it `groups = "smoke"` and run `mvn test -Psmoke`.

## Day 3 — scale
* Move data into `testdata/*.json|xlsx` and use `@TestData`.
* Add your pipeline (`Jenkinsfile` / `.github/workflows/qverse-ci.yml` are ready).
* Parallelism: adjust `thread-count` in suite XML; each thread gets its own browser.

## Adapting to a domain (examples)
| Domain | Pages / Screens | APIs |
|---|---|---|
| Banking | `LoginPage`, `TransferScreen`, `StatementPage` | `AccountAPI`, `PaymentAPI` |
| Insurance | `QuotePage`, `ClaimPage` | `PolicyAPI`, `ClaimAPI` |
| Telecom | `PlanSelectionPage`, `SimActivationScreen` | `SubscriberAPI`, `BillingAPI` |
| Healthcare | `PatientRegistrationPage`, `AppointmentScreen` | `PatientAPI`, `FhirAPI` |
| Retail / ERP / CRM | `CheckoutPage`, `PurchaseOrderPage`, `LeadPage` | `OrderAPI`, `InventoryAPI`, `ContactAPI` |

The framework (`src/main`) never changes between domains — only the DSL in `src/test`.

## Checklist before your first PR
- [ ] No `Thread.sleep`, no raw `driver.findElement` in tests
- [ ] Locators are private and business-named
- [ ] Test names describe business outcomes
- [ ] Test data outside the code, secrets in env vars
- [ ] `mvn test -Psmoke` green locally
