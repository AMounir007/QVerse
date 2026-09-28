package com.mounir.learn.qverse.data;

import com.mounir.learn.qverse.core.exception.TestDataException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <b>Factory</b> selecting the right {@link TestDataReader} by file type. New sources are
 * registered with {@link #register(TestDataReader)} - open for extension, closed for modification.
 */
public final class TestDataFactory {

    private static final List<TestDataReader> READERS =
            new CopyOnWriteArrayList<>(List.of(new JsonDataReader(), new ExcelDataReader()));
    private static final JsonDataReader JSON = new JsonDataReader();

    private TestDataFactory() {
    }

    public static void register(TestDataReader reader) {
        READERS.add(0, reader);
    }

    public static List<Map<String, Object>> rows(String source, String sheet) {
        return READERS.stream().filter(r -> r.supports(source)).findFirst()
                .orElseThrow(() -> new TestDataException("No data reader supports " + source))
                .readRows(source, sheet);
    }

    /** Maps a JSON file directly to a business object, e.g. {@code load("testdata/transfer.json", Transfer.class)}. */
    public static <T> T load(String source, Class<T> type) {
        return JSON.read(source, type);
    }
}
