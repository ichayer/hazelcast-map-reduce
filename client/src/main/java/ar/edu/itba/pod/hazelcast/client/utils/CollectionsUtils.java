package ar.edu.itba.pod.hazelcast.client.utils;

import java.util.Collection;
import java.util.Map;

public class CollectionsUtils {

    private static final String MESSAGE = "Collection can't be empty";

    public static <T> Collection<T> requireNotEmpty(Collection<T> collection) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(MESSAGE);
        }
        return collection;
    }

    public static <K, V> Map<K, V> requireNotEmpty(Map<K, V> map) {
        if (map.isEmpty()) {
            throw new IllegalArgumentException(MESSAGE);
        }
        return map;
    }

    private CollectionsUtils() {
        throw new AssertionError();
    }
}
