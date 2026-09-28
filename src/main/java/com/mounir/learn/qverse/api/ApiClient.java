package com.mounir.learn.qverse.api;

import com.mounir.learn.qverse.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.http.Method;
import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * <b>Singleton</b> HTTP client. It is stateless (each call builds its own RequestSpecification),
 * so one instance is safely shared by all parallel threads. Every request/response is automatically
 * attached to Allure (AllureRestAssured) and logged (ApiLoggingFilter).
 */
public final class ApiClient {

    private static final ApiClient INSTANCE = new ApiClient();

    private ApiClient() {
        RestAssured.config = RestAssured.config()
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                        .jackson2ObjectMapperFactory((Jackson2ObjectMapperFactory) (type, charset) ->
                                new com.fasterxml.jackson.databind.ObjectMapper().findAndRegisterModules()));
    }

    public static ApiClient instance() {
        return INSTANCE;
    }

    public Response send(Method method, ApiRequest request) {
        String baseUri = request.getBaseUri() != null ? request.getBaseUri() : ConfigManager.config().apiBaseUrl();
        RequestSpecification spec = RestAssured.given()
                .baseUri(baseUri)
                .contentType(request.getContentType())
                .headers(request.getHeaders())
                .queryParams(request.getQueryParams())
                .pathParams(request.getPathParams())
                .filter(new ApiLoggingFilter())
                .filter(new AllureRestAssured()
                        .setRequestAttachmentName("API Request")
                        .setResponseAttachmentName("API Response"));
        if (request.getBody() != null) {
            spec.body(request.getBody());
        }
        return spec.when().request(method, request.getPath()).then().extract().response();
    }
}
