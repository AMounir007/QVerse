# QVerse — Tester Guide (no deep coding required)

## Your test is a manual test case written in code
```java
public class TransferTest extends WebTest {           // WebTest | ApiTest | MobileTest
    @Test(description = "Customer transfers money")
    public void customerTransfersMoney() {
        LoginPage.open()
                 .enterUsername("admin")
                 .enterPassword("password")
                 .clickLogin()
                 .verifyTitle("Products");
    }
}
```
You never write waits, drivers, `Thread.sleep`, try/catch, screenshots or logs — QVerse does it.

## Vocabulary cheat-sheet
| Web | API | Mobile |
|---|---|---|
| `open(url)` | `get(path)` | `tap(x)` |
| `click(x)` / `doubleClick(x)` | `post(request)` | `sendKeys(x, text)` |
| `type(x, text)` / `typeSecret(x, pwd)` | `put / patch / delete` | `swipe(Direction.UP)` |
| `select(x, option)` | `verifyStatusCode(200)` | `scroll(x, Direction.UP, 5)` |
| `hover(x)` / `dragAndDrop(a, b)` | `verifyResponseBody("name", "John")` | `pinch(x)` / `zoom(x)` |
| `scroll(x)` / `uploadFile(x, "files/a.pdf")` | `verifySchema("schemas/x.json")` | `verifyElement(x)` |
| `verifyText(x, "..")` / `verifyDisplayed(x)` | `verifyBodyContains("..")` | `verifyText(x, "..")` |
| `verifyFileDownloaded("report.pdf")` | `extract("id")` | `hideKeyboard()` |

Quick access without page objects: `QVerse.web()`, `QVerse.api()`, `QVerse.mobile()`.

## Data-driven tests (JSON or Excel)
```java
@TestData("testdata/users.json")              // or "testdata/users.xlsx", sheet = "Valid"
@Test(dataProvider = "testData", dataProviderClass = QVerseDataProviders.class)
public void login(Map<String, Object> user) { ... user.get("username") ... }
```
Excel: first row = column names; each following row = one test run.

## Sharing values between steps
`QVerse.remember("customerId", id);` … `int id = QVerse.recall("customerId");` (safe in parallel).

## Running
* IDE: right-click the test → Run.
* Command line: `mvn test -Psmoke` · `mvn test -Pstaging` · `mvn test -Dbrowser=EDGE -Pheadless`.
* Report: `mvn allure:serve` — every step, screenshot, log, API request/response and a *failure insight* telling you if it is a product bug, a locator change or an environment problem.

## Reading a failure
Open the failed test in Allure → **QVerse failure insight** attachment:
* `ASSERTION` / `API_CONTRACT` → likely product defect → raise a bug.
* `LOCATOR` → UI changed → ask for the locator update (or add `.orElse(...)` fallback).
* `SYNCHRONIZATION` / `ENVIRONMENT` → re-run / check infrastructure.
