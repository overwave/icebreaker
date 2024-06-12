package dev.overwave.icebreaker.core.graph;

import dev.overwave.icebreaker.core.util.ListUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SparseList<T> {
    public static final SparseList<Void> EMPTY = new SparseList<>(0, List.of());

    @Getter
    private final int sparseFactor;
    private final List<T> list;

    public SparseList(int sparseFactor, List<T> list) {
        this.sparseFactor = sparseFactor;
        this.list = new ArrayList<>(list);
    }

    public T getSparse(int index) {
        int factor = 1 << sparseFactor;
        if (index % factor != 0) {
            return null;
        }
        return get(index >> sparseFactor);
    }

    public T getClosestSparse(int index) {
        int factor = 1 << sparseFactor;
        if (index % factor != 0) {
            for (int offset = 1; offset < factor; offset++) {
                T right = getSparse(index + offset);
                if (right != null) {
                    return right;
                }
                T left = getSparse(index - offset);
                if (left != null) {
                    return left;
                }
            }
            return null;
        }
        return get(index >> sparseFactor);
    }

    public T get(int index) {
        return ListUtils.getOrNull(list, index);
    }

    public List<T> getContent() {
        return Collections.unmodifiableList(list);
    }

    @SuppressWarnings("unchecked")
    public static <T> SparseList<T> empty() {
        return (SparseList<T>) EMPTY;
    }
}
