package dev.overwave.icebreaker.core.util;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class ListUtils {
    public <T> T getOrNull(List<T> list, int index) {
        if (list == null || index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    public <T> T getOrDefault(List<T> list, int index, T other) {
        T result = getOrNull(list, index);
        return result != null ? result : other;
    }
}
