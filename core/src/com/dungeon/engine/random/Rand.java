package com.dungeon.engine.random;

import java.util.List;
import java.util.Random;

public class Rand {

    private static final Random random = new Random();

    public static boolean chance(float chance) {
        return random.nextFloat() < chance;
    }

    public static float between(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    public static int between(int min, int max) {
        return min + random.nextInt(max - min);
    }

    public static float nextFloat(float bound) {
        return random.nextFloat() * bound;
    }

    public static int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public static <T> T pick(T[] array) {
        return array[random.nextInt(array.length)];
    }

    public static <T> T pick(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}
