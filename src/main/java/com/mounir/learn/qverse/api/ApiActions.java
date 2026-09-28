package com.mounir.learn.qverse.api;

import com.mounir.learn.qverse.reporting.ReportManager;
import io.restassured.http.Method;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.Objects;

/**
 * <b>Facade</b> for API testing: send request -> validate response, fluently.
 * <pre>
 * new ApiActions().get("/users/1").verifyStatusCode(200).verifyResponseBody("name", "Leanne Graham");
 * </pre>
 * One instance represents one conversation step; it is not shared between threads.
 */
public class ApiActions {

    private final ApiClient client = ApiClient.instance();
    private Response response;

    // ---------------- Requests ----------------

    public ApiActions get(String path) { return get(ApiRequest.to(path)); }
    public ApiActions get(ApiRequest request) { return send(Method.GET, request); }

    public ApiActions post(ApiRequest request) { return send(Method.POST, request); }
    public ApiActions put(ApiRequest request) { return send(Method.PUT, request); }
    public ApiActions patch(ApiRequest request) { return send(Method.PATCH, request); }

    public ApiActions delete(String path) { return delete(ApiRequest.to(path)); }
    public ApiActions delete(ApiRequest request) { return send(Method.DELETE, request); }

    private ApiActions send(Method method, ApiRequest request) {
        response = ReportManager.stepAndReturn("Send " + method + " " + request.getPath(),
                () -> client.send(method, request));
        return this;
    }

    // ---------------- Verifications ----------------

    public ApiActions verifyStatusCode(int expected) {
        ReportManager.step("Verify status code is " + expected, () -> Assert.assertEquals(
                response().statusCode(), expected, "Unexpected status code. Body: " + response().asPrettyString()));
        return this;
    }

    /** Verifies a JSON field (GPath, e.g. "data.customer.name") equals the expected value. */
    public ApiActions verifyResponseBody(String jsonPath, Object expected) {
        ReportManager.step("Verify '" + jsonPath + "' equals '" + expected + "'", () -> {
            Object actual = response().jsonPath().get(jsonPath);
            Assert.assertEquals(Objects.toString(actual), Objects.toString(expected),
                    "Unexpected value at '" + jsonPath + "'");
        });
        return this;
    }

    public ApiActions verifyBodyContains(String text) {
        ReportManager.step("Verify response contains '" + text + "'", () -> Assert.assertTrue(
                response().asString().contains(text), "Response does not contain '" + text + "'"));
        return this;
    }

    /** Validates the response against a JSON schema on the classpath (contract testing). */
    public ApiActions verifySchema(String classpathSchema) {
        ReportManager.step("Verify response matches schema " + classpathSchema, () -> response().then()
                .assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath(classpathSchema)));
        return this;
    }

    public ApiActions verifyResponseTimeBelow(long millis) {
        ReportManager.step("Verify response time below " + millis + " ms", () -> Assert.assertTrue(
                response().time() < millis, "Response took " + response().time() + " ms"));
        return this;
    }

    // ---------------- Extraction ----------------

    public <T> T extract(String jsonPath) {
        return response().jsonPath().get(jsonPath);
    }

    public <T> T as(Class<T> type) {
        return response().as(type);
    }

    public Response response() {
        if (response == null) {
            throw new IllegalStateException("No request sent yet - call get()/post()/... first.");
        }
        return response;
    }
}
