package io.js.J2V8Interop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CollectorEx {
    public static <T> Collector<T, ?, T> singletonCollector() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException("Excpected a single element, but got " + list.size());
                    }
                    return list.get(0);
                }
        );
    }
}
