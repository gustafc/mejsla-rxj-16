package gustafc.util;

import rx.Observable;

import java.util.Iterator;
import java.util.Objects;

public class Zip {

    public static <T> Observable<Indexed<T>> withIndex(Observable<? extends T> values) {
        return values.zipWith(cycleInts(), (v, i) -> new Indexed<>(i, v));
    }

    private static Iterable<Integer> cycleInts() {
        class CycledInts implements Iterator<Integer> {
            private int current = 0;
            @Override
            public boolean hasNext() { return true; }

            @Override
            public Integer next() { return current++; }
        }
        return CycledInts::new;
    }

    public static final class Indexed<T> {
        public final int index;
        public final T value;

        public Indexed(int index, T value) {
            this.index = index;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Indexed)) return false;
            Indexed<?> that = (Indexed<?>) obj;
            return this.index == that.index && Objects.equals(this.value, that.value);
        }

        @Override
        public String toString() {
            return "Indexed{" + "index=" + index + ", value=" + value + '}';
        }
    }
}
