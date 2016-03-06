package gustafc.util;

import org.junit.Test;
import rx.Observable;

import java.util.Optional;

import static gustafc.testutil.ObservableMatchers.emitsValues;
import static org.junit.Assert.assertThat;

public class ObservableFactoriesTest {

    @Test
    public void shouldGenerateIndefinitely() {
        assertThat(ObservableFactories.generate(1, n -> n + 1).take(4), emitsValues(1, 2, 3, 4));
    }

    @Test
    public void shouldGenerateUntilAbsentIsReturned() {
        Observable<String> substrings = ObservableFactories.generateUntil("fikon",
                s -> s.isEmpty() ? Optional.empty() : Optional.of(s.substring(1)));
        assertThat(substrings, emitsValues("fikon", "ikon", "kon", "on", "n", ""));
    }

}