package com.mounir.learn.qverse.testng;

import com.mounir.learn.qverse.core.driver.DriverManager;

/** Base class for mobile tests - an Appium session per test method. */
public abstract class MobileTest extends BaseTest {
    @Override
    protected void startSession() {
        DriverManager.startMobile();
    }
}
