package com.mounir.learn.qverse.testng;

/** Base class for API tests - no UI session, so API suites run fast and massively parallel. */
public abstract class ApiTest extends BaseTest {
    @Override
    protected void startSession() {
        // API tests are session-less; ApiClient is a thread-safe singleton.
    }
}
