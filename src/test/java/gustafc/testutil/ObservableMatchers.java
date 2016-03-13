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

    public static <T> Matcher<Observable<? extends T>> emitsValue(T value) {
        return emitsValues(value);
    }

    @SafeVarargs
    public static <T> Matcher<Observable<? extends T>> emitsValues(T... expected) {
        return new BaseMatcher<Observable<? extends T>>() {
            List<T> actual;

            @Override
            public boolean matches(Object item) {
                try {
                    actual = valuesIn(((Observable<T>) item).take(expected.length + 1));
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
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

    public static Matcher<Observable<?>> failsWith(Class<? extends Throwable> exceptionClass) {
        return new BaseMatcher<Observable<?>>() {

            private List<?> found;
            private Throwable actuallyThrown;

            @Override
            public boolean matches(Object item) {
                try {
                    found = valuesIn(((Observable<?>) item).takeLast(5));
                    return false;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    actuallyThrown = e.getCause();
                    return exceptionClass.isInstance(actuallyThrown);
                }
            }

            @Override
            public void describeMismatch(Object item, Description description) {
                if (found != null) {
                    description.appendText("completed successfully, last items emitted were: " + found);
                } else if (actuallyThrown != null) {
                    description.appendText("failed with " + actuallyThrown);
                } else { // wtf?
                    super.describeMismatch(item, description);
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("An observable failing with: " + exceptionClass.getCanonicalName());
            }

        };
    }

    private static <T> List<T> valuesIn(Observable<T> obs) throws ExecutionException, InterruptedException {
        return obs.toList().toBlocking().toFuture().get();
    }

}
