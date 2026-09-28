package com.mounir.learn.qverse.examples.web.pages;

import com.mounir.learn.qverse.business.BasePage;
import com.mounir.learn.qverse.core.locator.Locator;

/** Business DSL for the login screen. Locators are private: tests only see business actions. */
public final class LoginPage extends BasePage<LoginPage> {

    private static final Locator USERNAME = Locator.id("Username field", "user-name")
            .orElse(Locator.css("Username field (fallback)", "input[data-test='username']"));
    private static final Locator PASSWORD = Locator.id("Password field", "password");
    private static final Locator LOGIN_BUTTON = Locator.id("Login button", "login-button");
    private static final Locator ERROR_MESSAGE = Locator.css("Error message", "[data-test='error']");

    private LoginPage() {
    }

    public static LoginPage open() {
        return new LoginPage().load();
    }

    @Override
    protected String path() {
        return "/";
    }

    @Override
    protected Locator pageIdentity() {
        return LOGIN_BUTTON;
    }

    public LoginPage enterUsername(String username) {
        web.type(USERNAME, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        web.typeSecret(PASSWORD, password);
        return this;
    }

    /** Successful login - navigates to the products page. */
    public ProductsPage clickLogin() {
        web.click(LOGIN_BUTTON);
        return new ProductsPage().verifyLoaded();
    }

    /** Login attempt expected to be rejected - stays on this page. */
    public LoginPage clickLoginExpectingError() {
        web.click(LOGIN_BUTTON);
        return this;
    }

    public ProductsPage loginAs(String username, String password) {
        return enterUsername(username).enterPassword(password).clickLogin();
    }

    public LoginPage verifyErrorMessage(String expected) {
        web.verifyText(ERROR_MESSAGE, expected);
        return this;
    }
}
