package com.dungeon.engine.util;

import sun.misc.SharedSecrets;

import java.util.Collection;
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

    public static void doBetween(int min, int max, Runnable runnable) {
        int times = between(min, max);
        for (int i = 0; i <= times; ++i) {
            runnable.run();
        }
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

    public static <T> T pick(Collection<T> collection) {
        return collection.stream().skip(random.nextInt(collection.size())).findFirst().get();
    }

    public static <T extends Enum<T>> T pick(Class<T> enumClass) {
        return Rand.pick(SharedSecrets.getJavaLangAccess()
                .getEnumConstantsShared(enumClass));
    }

}
