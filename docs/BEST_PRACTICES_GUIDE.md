# QVerse — Best Practices Guide

## Test design
1. **One business outcome per test**; name it as the outcome: `customerCanTransferBetweenOwnAccounts`.
2. **Tests speak business, pages speak UI.** No locators, URLs or status codes in test classes.
3. **Independent tests**: each test creates its own data (`RandomData`, API setup) — required for parallelism.
4. **Arrange via API, assert via UI**: create preconditions with `CustomerAPI` rather than long UI flows.
5. Tag with groups: `smoke` (minutes, every commit), `regression` (nightly), domain tags (`payments`, `claims`).

## Locators
* Priority: `id` / `data-test` / `accessibilityId` › CSS › XPath. Avoid index-based XPath.
* Always give a **business name**: `Locator.id("Submit claim button", "submitClaim")`.
* Use templates for lists/tables: `ROW.with(customerName)`.
* Add `.orElse(...)` fallbacks for volatile elements; a `SELF-HEALED` warning in logs means "update the primary".

## Synchronisation & resilience
* Never `Thread.sleep`. Actions already wait for the right state.
* Retries (action + test) absorb *infrastructure* flakiness only; a test that passes only on retry is shown as RETRIED on the dashboard — fix it, don't ignore it.
* Keep `test.retry.count=0` on prod smoke to avoid hiding issues.

## Data & configuration
* Environment-specific values live in `env/<env>.properties`; secrets **only** in environment variables / CI vault.
* Use `typeSecret` for passwords/tokens so they never appear in reports.
* Prefer JSON for structured data, Excel for tester-maintained tables.

## API testing
* Validate **status + key fields + schema** (contract) for each endpoint.
* Keep endpoint paths and payload builders in service classes (`CustomerAPI`), never in tests.
* Use `verifyResponseTimeBelow` for SLA smoke checks.

## Parallel execution
* No static mutable state in tests or pages. Use `QVerse.remember/recall`.
* Size `thread-count` to grid/device capacity; API suites can go much higher than UI suites.

## Reporting
* Use `@Epic/@Feature/@Story/@Severity` so Allure dashboards map to business capabilities.
* Treat the dashboard `passRate` + failure categories as quality KPIs in CI gates.

## Framework governance
* `src/main` is a product: versioned, reviewed by the framework team, backwards-compatible.
* New capabilities go through extension points (Strategy/SPI/Factory) — don't fork core classes.
* Keep dependencies centralised in `pom.xml` properties; upgrade quarterly and run CVE checks.
