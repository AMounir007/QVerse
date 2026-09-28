package com.mounir.learn.qverse.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mounir.learn.qverse.core.exception.QVerseException;

/** JSON helpers for building payloads and pretty-printing in reports. */
public final class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private JsonUtils() {
    }

    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new QVerseException("Cannot serialise " + value, e);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new QVerseException("Cannot parse JSON into " + type.getSimpleName(), e);
        }
    }
}
