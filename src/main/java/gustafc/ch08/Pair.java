package gustafc.ch08;

import rx.Observable;
import rx.functions.Func2;

import java.util.Objects;

import static gustafc.util.Cast.tryCast;

public class Pair<A, B> {

    public final A left;
    public final B right;

    public Pair(A left, B right) {
        this.left = left;
        this.right = right;
    }

    public <R> R use(Func2<? super A, ? super B, ? extends R> mapper) {
        return mapper.call(left, right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                tryCast(Pair.class, obj)
                        .map(that -> Objects.equals(this.left, that.left) && Objects.equals(this.right, that.right))
                        .orElse(false);
    }

    @Override
    public String toString() {
        return "(" + left + ", " + right + ')';
    }

    public static <T> Observable.Transformer<T, Pair<T, T>> pairs() {
        return obs -> obs.scan(
                (Pair<T, T>) null,
                (previous, value) -> new Pair<>(previous == null ? null : previous.right, value)
        ).skip(2);
    }

    public static <T, R> Observable.Transformer<T, R> pairMap(Func2<? super T, ? super T, ? extends R> mapper) {
        return obs -> obs.compose(pairs()).map(pair -> pair.use(mapper));
    }

}
