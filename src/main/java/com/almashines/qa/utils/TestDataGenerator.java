package com.almashines.qa.utils;

import java.util.Random;

/**
 * TestDataGenerator provides helper utilities for dynamic test data creation.
 */
public class TestDataGenerator {

    private static final Random random = new Random();

    /**
     * Generates a unique email address with a timestamp suffix.
     * @return String email address.
     */
    public static String generateRandomEmail() {
        return "sdet_candidate_" + System.currentTimeMillis() + "@yopmail.com";
    }

    /**
     * Generates a repeating character string of an exact length.
     * Useful for boundary testing.
     * @param length Required string length.
     * @return String of repeating characters.
     */
    public static String generateStringOfLength(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("a");
        }
        return sb.toString();
    }

    /**
     * Generates a random numeric string for mobile testing.
     * @param length String length (e.g. 10 digits).
     * @return String numeric characters.
     */
    public static String generateNumericString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
