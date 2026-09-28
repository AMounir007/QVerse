package com.mounir.learn.qverse.testng;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declarative data binding for tests:
 * <pre>
 * &#64;TestData("testdata/users.json")
 * &#64;Test(dataProvider = "testData", dataProviderClass = QVerseDataProviders.class)
 * public void login(Map&lt;String, Object&gt; user) { ... }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestData {

    /** Classpath or file path to a .json / .xlsx file. */
    String value();

    /** Excel sheet name, or JSON top-level key. Empty = first sheet / root array. */
    String sheet() default "";
}
