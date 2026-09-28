package com.mounir.learn.qverse.examples.web;

import com.mounir.learn.qverse.examples.web.pages.LoginPage;
import com.mounir.learn.qverse.testng.QVerseDataProviders;
import com.mounir.learn.qverse.testng.TestData;
import com.mounir.learn.qverse.testng.WebTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import java.util.Map;

/** Web example: tests read like a manual test case. No waits, drivers, or locators here. */
@Epic("Web")
@Feature("Authentication")
public class WebLoginTest extends WebTest {

    @Test(groups = "smoke", description = "Valid user logs in and buys a product")
    @Severity(SeverityLevel.BLOCKER)
    @Description("A registered customer logs in, sees the catalogue and adds a product to the cart.")
    public void customerCanLoginAndAddProductToCart() {
        LoginPage.open()
                .enterUsername("standard_user")
                .enterPassword("secret_sauce")
                .clickLogin()
                .verifyTitle("Products")
                .sortBy("Price (low to high)")
                .addToCart("Sauce Labs Backpack")
                .verifyCartCount(1);
    }

    @TestData("testdata/users.json")
    @Test(dataProvider = "testData", dataProviderClass = QVerseDataProviders.class,
            description = "Data-driven login for every user profile")
    public void loginBehavesPerUserProfile(Map<String, Object> user) {
        String username = (String) user.get("username");
        String password = (String) user.get("password");

        if (Boolean.TRUE.equals(user.get("valid"))) {
            LoginPage.open().loginAs(username, password).verifyTitle("Products");
        } else {
            LoginPage.open()
                    .enterUsername(username)
                    .enterPassword(password)
                    .clickLoginExpectingError()
                    .verifyErrorMessage((String) user.get("error"));
        }
    }
}
