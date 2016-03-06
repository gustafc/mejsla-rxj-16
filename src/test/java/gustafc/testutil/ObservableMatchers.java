package gustafc.testutil;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import rx.Observable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ObservableMatchers {


    public static <T> Matcher<Observable<? extends T>> emitsNothing() {
        return emitsValues();
    }

    @SafeVarargs
    public static <T> Matcher<Observable<? extends T>> emitsValues(T... expected) {
        return new BaseMatcher<Observable<? extends T>>() {
            List<T> actual;

            @Override
            public boolean matches(Object item) {
                actual = valuesIn((Observable<T>) item, expected.length + 1);
                return Arrays.equals(expected, actual.toArray(new Object[0]));
            }

            @Override
            public void describeMismatch(Object item, Description description) {
                String label = expected.length == actual.size()? "was " :
                        "was (too " + (expected.length > actual.size() ? "short" : "long") + ")";
                description.appendText(label + " ").appendValue(actual);
            }

            @Override
            public void describeTo(Description description) {
                description.appendValueList("An observable emitting: [", ", ", "]", Arrays.asList(expected));
            }

        };
    }

    private static <T> List<T> valuesIn(Observable<T> obs, int max) {
        try {
            return obs.take(max).toList().toBlocking().toFuture().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
