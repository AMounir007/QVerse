package com.mounir.learn.qverse.examples.api;

import com.mounir.learn.qverse.api.ApiActions;
import com.mounir.learn.qverse.business.BaseApi;
import com.mounir.learn.qverse.examples.api.model.Customer;

/**
 * Business DSL for the Customer service. Endpoints, payloads and status codes live here once;
 * tests just say {@code CustomerAPI.createCustomer().shouldBeCreated()}.
 */
public final class CustomerAPI extends BaseApi {

    private static final String CUSTOMERS = "/users";
    private static final String CUSTOMER = "/users/{id}";
    private static final String SCHEMA = "schemas/customer-schema.json";

    private ApiActions exchange;
    private Customer sent;

    private CustomerAPI() {
    }

    // -------- Business operations --------

    public static CustomerAPI createCustomer() {
        return createCustomer(Customer.random());
    }

    public static CustomerAPI createCustomer(Customer customer) {
        CustomerAPI api = new CustomerAPI();
        api.sent = customer;
        api.exchange = api.api().post(api.request(CUSTOMERS).body(customer).build());
        return api;
    }

    public static CustomerAPI findCustomer(int id) {
        CustomerAPI api = new CustomerAPI();
        api.exchange = api.api().get(api.request(CUSTOMER).pathParam("id", id).build());
        return api;
    }

    public static CustomerAPI updateCustomerEmail(int id, String email) {
        CustomerAPI api = new CustomerAPI();
        api.exchange = api.api().patch(api.request(CUSTOMER).pathParam("id", id)
                .body(Customer.builder().email(email).build()).build());
        return api;
    }

    public static CustomerAPI deleteCustomer(int id) {
        CustomerAPI api = new CustomerAPI();
        api.exchange = api.api().delete(api.request(CUSTOMER).pathParam("id", id).build());
        return api;
    }

    // -------- Business expectations --------

    public CustomerAPI shouldBeCreated() {
        exchange.verifyStatusCode(201)
                .verifyResponseBody("name", sent.getName())
                .verifyResponseBody("email", sent.getEmail());
        return this;
    }

    public CustomerAPI shouldExist() {
        exchange.verifyStatusCode(200).verifySchema(SCHEMA).verifyResponseTimeBelow(5000);
        return this;
    }

    public CustomerAPI shouldHaveName(String name) {
        exchange.verifyResponseBody("name", name);
        return this;
    }

    public CustomerAPI shouldHaveEmail(String email) {
        exchange.verifyStatusCode(200).verifyResponseBody("email", email);
        return this;
    }

    public CustomerAPI shouldBeDeleted() {
        exchange.verifyStatusCode(200);
        return this;
    }

    public int customerId() {
        return exchange.extract("id");
    }
}
