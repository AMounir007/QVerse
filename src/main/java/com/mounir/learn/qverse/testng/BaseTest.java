package com.mounir.learn.qverse.testng;

import com.mounir.learn.qverse.core.context.QVerseContext;
import com.mounir.learn.qverse.core.driver.DriverManager;
import com.mounir.learn.qverse.core.logging.QVerseLogger;
import org.apache.logging.log4j.ThreadContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * <b>Template Method pattern</b> for the test lifecycle. The final setUp/tearDown methods guarantee
 * context isolation and session cleanup; subclasses only decide which session to start.
 */
public abstract class BaseTest {

    protected final QVerseLogger log = QVerseLogger.getLogger(getClass());

    @BeforeMethod(alwaysRun = true)
    public final void qverseSetUp(Method method) {
        String testName = getClass().getSimpleName() + "." + method.getName();
        QVerseContext.start(testName);
        ThreadContext.put("testName", testName);
        startSession();
        beforeEachTest();
    }

    @AfterMethod(alwaysRun = true)
    public final void qverseTearDown(ITestResult result) {
        try {
            afterEachTest(result);
        } finally {
            endSession();
            QVerseContext.clear();
            ThreadContext.clearAll();
        }
    }

    /** Starts the platform session (browser, device, nothing for API). */
    protected abstract void startSession();

    protected void endSession() {
        DriverManager.quitAll();
    }

    protected void beforeEachTest() {
    }

    protected void afterEachTest(ITestResult result) {
    }
}
