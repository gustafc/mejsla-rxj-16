package gustafc.util;

import rx.Observable;
import rx.Subscriber;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Zip {

    public static <T> Observable<Indexed<T>> withIndex(Observable<? extends T> values) {
        return values.lift(Zip.withIndex());
    }

    public static <T> Observable.Operator<Indexed<T>, T> withIndex() {
        return subscriber -> new Subscriber<T>(subscriber) {
            final AtomicLong counter = new AtomicLong(0);

            @Override
            public void onCompleted() {
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onNext(T t) {
                subscriber.onNext(new Indexed<>(counter.getAndIncrement(), t));
            }
        };
    }

    public static final class Indexed<T> {
        public final long index;
        public final T value;

        public Indexed(long index, T value) {
            this.index = index;
            this.value = value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, value);
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
