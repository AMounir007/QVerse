package com.mounir.learn.qverse.examples.api;

import com.mounir.learn.qverse.QVerse;
import com.mounir.learn.qverse.testng.ApiTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

/** API example: CRUD + contract validation expressed as business statements. */
@Epic("API")
@Feature("Customer management")
public class CustomerApiTest extends ApiTest {

    @Test(groups = "smoke", description = "A new customer can be registered")
    public void newCustomerCanBeRegistered() {
        CustomerAPI.createCustomer().shouldBeCreated();
    }

    @Test(description = "An existing customer matches the published contract")
    public void existingCustomerMatchesContract() {
        CustomerAPI.findCustomer(1).shouldExist().shouldHaveName("Leanne Graham");
    }

    @Test(description = "Customer email can be updated")
    public void customerEmailCanBeUpdated() {
        CustomerAPI.updateCustomerEmail(1, "new.mail@example.com").shouldHaveEmail("new.mail@example.com");
    }

    @Test(description = "Customer can be removed")
    public void customerCanBeRemoved() {
        CustomerAPI.deleteCustomer(1).shouldBeDeleted();
    }

    @Test(description = "Low-level facade for ad-hoc checks")
    public void rawApiFacadeExample() {
        QVerse.api()
                .get("/users/2")
                .verifyStatusCode(200)
                .verifyResponseBody("username", "Antonette");
    }
}
