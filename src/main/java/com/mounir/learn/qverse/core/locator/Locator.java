package com.mounir.learn.qverse.core.locator;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Immutable, business-named locator that works for Web AND Mobile.
 * <ul>
 *   <li><b>Business name</b> - appears in reports/logs ("Click on 'Login button'").</li>
 *   <li><b>Dynamic templates</b> - {@code ROW.with("John")} formats %s placeholders.</li>
 *   <li><b>Fallbacks</b> - {@code orElse(...)} feeds the self-healing engine.</li>
 * </ul>
 */
public final class Locator {

    public enum Type { ID, CSS, XPATH, NAME, LINK_TEXT, TEXT, ACCESSIBILITY_ID, ANDROID_UIAUTOMATOR, IOS_PREDICATE }

    private final String name;
    private final Type type;
    private final String value;
    private final List<Locator> fallbacks;

    private Locator(String name, Type type, String value, List<Locator> fallbacks) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.value = Objects.requireNonNull(value);
        this.fallbacks = List.copyOf(fallbacks);
    }

    public static Locator of(String name, Type type, String value) {
        return new Locator(name, type, value, List.of());
    }

    public static Locator id(String name, String value) { return of(name, Type.ID, value); }
    public static Locator css(String name, String value) { return of(name, Type.CSS, value); }
    public static Locator xpath(String name, String value) { return of(name, Type.XPATH, value); }
    public static Locator byName(String name, String value) { return of(name, Type.NAME, value); }
    public static Locator linkText(String name, String value) { return of(name, Type.LINK_TEXT, value); }
    public static Locator text(String name, String value) { return of(name, Type.TEXT, value); }
    public static Locator accessibilityId(String name, String value) { return of(name, Type.ACCESSIBILITY_ID, value); }
    public static Locator uiAutomator(String name, String value) { return of(name, Type.ANDROID_UIAUTOMATOR, value); }
    public static Locator iosPredicate(String name, String value) { return of(name, Type.IOS_PREDICATE, value); }

    /** Adds a fallback locator used by the self-healing engine when the primary one fails. */
    public Locator orElse(Locator fallback) {
        List<Locator> all = new ArrayList<>(fallbacks);
        all.add(fallback);
        return new Locator(name, type, value, all);
    }

    /** Resolves a dynamic locator template, e.g. {@code "//tr[td='%s']"}. */
    public Locator with(Object... args) {
        List<Locator> resolved = fallbacks.stream().map(f -> f.with(args)).toList();
        return new Locator(safeFormat(name, args), type, safeFormat(value, args), resolved);
    }

    private static String safeFormat(String template, Object[] args) {
        return template.contains("%") ? String.format(template, args) : template + " " + Arrays.toString(args);
    }

    public By toBy() {
        return switch (type) {
            case ID -> By.id(value);
            case CSS -> By.cssSelector(value);
            case XPATH -> By.xpath(value);
            case NAME -> By.name(value);
            case LINK_TEXT -> By.linkText(value);
            case TEXT -> By.xpath("//*[normalize-space(text())='" + value + "']");
            case ACCESSIBILITY_ID -> AppiumBy.accessibilityId(value);
            case ANDROID_UIAUTOMATOR -> AppiumBy.androidUIAutomator(value);
            case IOS_PREDICATE -> AppiumBy.iOSNsPredicateString(value);
        };
    }

    public String name() { return name; }
    public Type type() { return type; }
    public String value() { return value; }
    public List<Locator> fallbacks() { return fallbacks; }

    @Override
    public String toString() {
        return "'" + name + "'";
    }
}
