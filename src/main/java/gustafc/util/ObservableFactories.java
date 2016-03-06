package gustafc.util;

import rx.Observable;

import java.util.Optional;
import java.util.function.Function;

public class ObservableFactories {

    public static <T> Observable<T> generate(T initial, Function<? super T, ? extends T> succ) {
        return generateUntil(initial, v -> Optional.of(succ.apply(v)));
    }

    public static <T> Observable<T> generateUntil(T initial, Function<? super T, Optional<T>> succ) {
        return forever()
                .scan(Optional.of(initial), (state, ignored) -> state.flatMap(succ))
                .takeWhile(Optional::isPresent)
                .map(Optional::get);
    }

    private static Observable<?> forever() {
        return Observable.just("forever").repeat();
    }

}
