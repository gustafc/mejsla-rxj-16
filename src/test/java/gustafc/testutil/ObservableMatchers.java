package gustafc.testutil;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import rx.Observable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ObservableMatchers {

    public static <T> Matcher<Observable<? extends T>> emitsValues(T... values) {
        return new BaseMatcher<Observable<? extends T>>() {
            List<T> items;

            @Override
            public boolean matches(Object item) {
                items = valuesIn((Observable<T>) item, values.length + 1);
                return Arrays.equals(values, items.toArray(new Object[0]));
            }

            @Override
            public void describeMismatch(Object item, Description description) {
                description.appendText("was ").appendValue(items);
            }

            @Override
            public void describeTo(Description description) {
                description.appendValueList("An observable emitting: [", ", ", "]", Arrays.asList(values));
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
