package com.mounir.learn.qverse.api;

import io.restassured.http.ContentType;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;

import java.util.Map;

/**
 * <b>Builder pattern</b> (Lombok) - an immutable, readable description of an HTTP call:
 * <pre>
 * ApiRequest.builder().path("/users/{id}").pathParam("id", 7).header("X-Trace", "abc").build();
 * </pre>
 */
@Getter
@ToString
@Builder(toBuilder = true)
public class ApiRequest {

    /** Optional override; defaults to api.base.url from configuration. */
    private final String baseUri;
    private final String path;
    @Singular
    private final Map<String, String> headers;
    @Singular
    private final Map<String, Object> queryParams;
    @Singular
    private final Map<String, Object> pathParams;
    private final Object body;
    @Builder.Default
    private final ContentType contentType = ContentType.JSON;

    public static ApiRequest to(String path) {
        return ApiRequest.builder().path(path).build();
    }
}
