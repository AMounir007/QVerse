# Changelog

All notable changes to QVerse are documented here.
Format follows [Keep a Changelog](https://keepachangelog.com/) and [Semantic Versioning](https://semver.org/).

## [1.0.1] — 2026-10-01

### 🚀 Highlights
First public, consumable release of QVerse via **JitPack** — build ✅ green.
Details: [docs/RELEASE_NOTES_1.0.1.md](docs/RELEASE_NOTES_1.0.1.md)

### ✅ Fixed
- JitPack build failure: `maven-compiler-plugin 3.13.0 requires Maven version 3.6.3`.
  Added `jitpack.yml` (JDK 21 + modern Maven) so the artifact builds successfully.
- Tests are skipped during JitPack publishing (`-DskipTests`) so the library builds without browsers/devices.

### 📦 Installation
```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>

<dependency>
  <groupId>com.github.AMounir007</groupId>
  <artifactId>QVerse</artifactId>
  <version>1.0.1</version>
</dependency>
```

### 📚 Documentation
- README: JitPack badge, installation and release process.
- Added this CHANGELOG.

### Requirements
Java 21 · Maven 3.6.3+

---

## [1.0.0] — 2026-10-01 (not usable)

### ✨ Initial framework
- Web automation (Selenium 4, WebDriverManager), self-healing & dynamic locators, smart waits/retries.
- API automation (REST Assured, JSON-schema validation, Allure traffic capture).
- Mobile automation (Appium java-client 9).
- TestNG suites: smoke, regression, web, api, mobile; parallel execution.
- Environment configs (qa / staging / prod) via OWNER; JSON/Excel test data.
- Allure reporting, categories, execution dashboard, Log4j2 logging.
- Selenium Grid docker-compose and Jenkins pipeline.
- Guides: Architecture, Developer, Tester, Onboarding, Best Practices.

### ⚠️ Known issue
- JitPack build failed (old Maven on build image). Fixed in **1.0.1**.
