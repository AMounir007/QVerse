package com.mounir.learn.qverse.testng;

import com.mounir.learn.qverse.config.ConfigManager;
import com.mounir.learn.qverse.core.ai.AiExtensions;
import com.mounir.learn.qverse.core.ai.AiFailureAnalyzer.FailureInsight;
import com.mounir.learn.qverse.core.logging.QVerseLogger;
import com.mounir.learn.qverse.reporting.AllureEnvironmentWriter;
import com.mounir.learn.qverse.reporting.ExecutionDashboard;
import com.mounir.learn.qverse.reporting.ReportManager;
import org.testng.IAnnotationTransformer;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Central TestNG listener: attaches screenshot + per-test log + AI failure insight on failure,
 * wires the retry analyzer into every test automatically, and publishes the dashboard.
 * Registered once in the suite XML - tests stay annotation-free.
 */
public class QVerseListener implements ITestListener, ISuiteListener, IAnnotationTransformer {

    private static final QVerseLogger LOG = QVerseLogger.getLogger(QVerseListener.class);

    @Override
    @SuppressWarnings("rawtypes")
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }

    @Override
    public void onStart(ISuite suite) {
        LOG.info("==== QVerse suite '{}' started on env '{}' ====", suite.getName(), ConfigManager.config().env());
        AllureEnvironmentWriter.write();
    }

    @Override
    public void onFinish(ISuite suite) {
        ExecutionDashboard.publish();
    }

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info(">>> START {}", name(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("<<< PASSED {}", name(result));
        ReportManager.attachText("Execution log", QVerseLogger.drainThreadLog());
        ExecutionDashboard.record(name(result), "PASSED", duration(result), null);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable failure = result.getThrowable();
        LOG.error("<<< FAILED " + name(result), failure);
        ReportManager.attachScreenshot("Failure screenshot");
        String log = QVerseLogger.drainThreadLog();
        ReportManager.attachText("Execution log", log);
        String category = null;
        if (failure != null && ConfigManager.config().failureAnalysisEnabled()) {
            FailureInsight insight = AiExtensions.failureAnalyzer().analyze(failure, log);
            ReportManager.attachText("QVerse failure insight", insight.toString());
            category = insight.category();
        }
        ExecutionDashboard.record(name(result), "FAILED", duration(result), category);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String status = result.wasRetried() ? "RETRIED" : "SKIPPED";
        if (result.wasRetried()) {
            ReportManager.attachScreenshot("Screenshot before retry");
        }
        ReportManager.attachText("Execution log", QVerseLogger.drainThreadLog());
        ExecutionDashboard.record(name(result), status, duration(result), null);
    }

    private static String name(ITestResult r) {
        return r.getTestClass().getRealClass().getSimpleName() + "." + r.getMethod().getMethodName();
    }

    private static long duration(ITestResult r) {
        return r.getEndMillis() - r.getStartMillis();
    }
}
