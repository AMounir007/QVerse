package com.mounir.learn.qverse.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mounir.learn.qverse.core.exception.TestDataException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * JSON test data (Jackson). Supports an array of rows, or an object of named data sets
 * (use {@code sheet} as the key, e.g. "validUsers").
 */
public final class JsonDataReader implements TestDataReader {

    static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public boolean supports(String source) {
        return source.toLowerCase().endsWith(".json");
    }

    @Override
    public List<Map<String, Object>> readRows(String source, String sheet) {
        try (InputStream in = DataStreams.open(source)) {
            JsonNode root = MAPPER.readTree(in);
            JsonNode rows = (sheet == null || sheet.isBlank()) ? root : root.path(sheet);
            if (!rows.isArray()) {
                throw new TestDataException("Expected a JSON array in " + source + (sheet.isBlank() ? "" : " at '" + sheet + "'"));
            }
            return MAPPER.convertValue(rows, new TypeReference<>() { });
        } catch (IOException e) {
            throw new TestDataException("Cannot read JSON test data " + source, e);
        }
    }

    public <T> T read(String source, Class<T> type) {
        try (InputStream in = DataStreams.open(source)) {
            return MAPPER.readValue(in, type);
        } catch (IOException e) {
            throw new TestDataException("Cannot map " + source + " to " + type.getSimpleName(), e);
        }
    }
}
