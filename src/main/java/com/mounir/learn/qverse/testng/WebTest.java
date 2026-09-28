package com.mounir.learn.qverse.testng;

import com.mounir.learn.qverse.core.driver.DriverManager;

/** Base class for browser tests - a fresh, isolated browser per test method. */
public abstract class WebTest extends BaseTest {
    @Override
    protected void startSession() {
        DriverManager.startWeb();
    }
}
