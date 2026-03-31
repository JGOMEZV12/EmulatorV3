package com.polar.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomNumber {

    private static final Random random = new Random();

    /**
     * Generates a new random number.
     *
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return Random generated number.
     */
    public static int generateNewRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * Generates a random number using a shared random class.
     *
     * @param min Minimum value (inclusive)
     * @param max Maximum value (exclusive)
     * @return Random generated number.
     */
    public static int generateLockedRandom(int min, int max) {
        synchronized (random) {
            return random.nextInt(max - min) + min;
        }
    }

    public static int generateRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
