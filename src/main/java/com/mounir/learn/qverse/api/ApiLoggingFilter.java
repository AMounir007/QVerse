package com.mounir.learn.qverse.api;

import com.mounir.learn.qverse.core.logging.QVerseLogger;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/** Logs every HTTP exchange (method, URI, status, latency) into the per-test log. */
public class ApiLoggingFilter implements Filter {

    private static final QVerseLogger LOG = QVerseLogger.getLogger(ApiLoggingFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification request, FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        LOG.info("--> {} {}", request.getMethod(), request.getURI());
        Response response = ctx.next(request, responseSpec);
        LOG.info("<-- {} {} ({} ms)", response.getStatusCode(), request.getURI(), response.getTime());
        return response;
    }
}
