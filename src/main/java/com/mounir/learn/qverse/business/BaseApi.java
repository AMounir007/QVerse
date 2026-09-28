package com.mounir.learn.qverse.business;

import com.mounir.learn.qverse.api.ApiActions;
import com.mounir.learn.qverse.api.ApiRequest;

import java.util.Map;

/**
 * <b>Template Method</b> base for business API services (CustomerAPI, PolicyAPI, OrderAPI...).
 * Common concerns (default headers, authentication) are defined once via overridable hooks.
 */
public abstract class BaseApi {

    /** Hook: override to add auth tokens, tenant ids, correlation ids... */
    protected Map<String, String> defaultHeaders() {
        return Map.of("Accept", "application/json");
    }

    protected ApiRequest.ApiRequestBuilder request(String path) {
        return ApiRequest.builder().path(path).headers(defaultHeaders());
    }

    protected ApiActions api() {
        return new ApiActions();
    }
}
