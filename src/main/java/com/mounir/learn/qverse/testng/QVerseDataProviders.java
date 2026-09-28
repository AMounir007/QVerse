package com.mounir.learn.qverse.testng;

import com.mounir.learn.qverse.core.exception.TestDataException;
import com.mounir.learn.qverse.data.TestDataFactory;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;

/** Generic data providers: any test gets data-driven iterations from a file with one annotation. */
public final class QVerseDataProviders {

    private QVerseDataProviders() {
    }

    /** Parallel data provider - each row runs on its own thread (dataproviderthreadcount). */
    @DataProvider(name = "testData", parallel = true)
    public static Object[][] testData(Method method) {
        return rows(method);
    }

    /** Sequential variant for data that must run in order. */
    @DataProvider(name = "testDataSequential")
    public static Object[][] testDataSequential(Method method) {
        return rows(method);
    }

    private static Object[][] rows(Method method) {
        TestData td = method.getAnnotation(TestData.class);
        if (td == null) {
            throw new TestDataException("Add @TestData(\"file\") to " + method.getName());
        }
        return TestDataFactory.rows(td.value(), td.sheet()).stream()
                .map(row -> new Object[]{row})
                .toArray(Object[][]::new);
    }
}
