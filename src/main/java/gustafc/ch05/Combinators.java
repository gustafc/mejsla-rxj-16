package gustafc.ch05;

import rx.Observable;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Combinators {

    public static <T> Observable<T> emitPeriodically(long delayMs, T... values) {
        return Observable.interval(delayMs, TimeUnit.MILLISECONDS).zipWith(
                Arrays.asList(values),
                (ignore, value) -> value);
    }

}
