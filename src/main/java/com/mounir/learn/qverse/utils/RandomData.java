package com.mounir.learn.qverse.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/** Collision-free synthetic data so parallel tests never fight over the same records. */
public final class RandomData {

    private RandomData() {
    }

    public static String uniqueId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static String uniqueEmail() {
        return "qverse." + uniqueId() + "@example.com";
    }

    public static String name() {
        String[] first = {"Alex", "Sam", "Jordan", "Taylor", "Morgan", "Casey"};
        String[] last = {"Smith", "Garcia", "Chen", "Khan", "Novak", "Silva"};
        var r = ThreadLocalRandom.current();
        return first[r.nextInt(first.length)] + " " + last[r.nextInt(last.length)];
    }

    public static String digits(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) sb.append(ThreadLocalRandom.current().nextInt(10));
        return sb.toString();
    }

    public static BigDecimal amount(double min, double max) {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(min, max)).setScale(2, RoundingMode.HALF_UP);
    }
}
