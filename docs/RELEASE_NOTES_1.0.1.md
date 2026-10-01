# QVerse 1.0.1 — Release Notes

**Release date:** 2026-10-01  
**Status:** ✅ Stable — published on [JitPack](https://jitpack.io/#AMounir007/QVerse/1.0.1)

## Summary
First public, consumable release of the QVerse automation ecosystem (Web · API · Mobile).
Any Maven project can now use QVerse as a dependency.

## What's new
- QVerse is available as a Maven dependency through JitPack.
- `jitpack.yml` added: builds with JDK 21 + Maven 3.9.9.
- README: JitPack badge, installation steps, release process.
- `CHANGELOG.md` and per-version release notes added.

## Fixed
- JitPack build failure in 1.0.0: `maven-compiler-plugin 3.13.0 requires Maven version 3.6.3`.
- Tests are skipped during publishing so the library builds without browsers/devices.

## Installation
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.AMounir007</groupId>
  <artifactId>QVerse</artifactId>
  <version>1.0.1</version>
</dependency>
```
Then run: `mvn clean install -U`

## Requirements
- Java 21
- Maven 3.6.3+

## Included capabilities
- Web: Selenium 4, self-healing & dynamic locators, smart waits/retries
- API: REST Assured, JSON-schema validation, Allure traffic capture
- Mobile: Appium java-client 9
- TestNG suites (smoke, regression, web, api, mobile), parallel execution
- Environments (qa / staging / prod), JSON/Excel test data
- Allure reports, execution dashboard, Log4j2 logging
- Selenium Grid docker-compose, Jenkins pipeline

## Upgrade notes
- Do **not** use `1.0.0` — it failed to build on JitPack. Use `1.0.1`.

## Known limitations
- Running the framework's own tests requires local browsers / Appium server / Grid.
