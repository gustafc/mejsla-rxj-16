package gustafc.ch04;

import org.junit.Test;
import rx.Observable;

import java.util.NoSuchElementException;

import static gustafc.testutil.ObservableMatchers.emitsNothing;
import static gustafc.testutil.ObservableMatchers.emitsValue;
import static gustafc.testutil.ObservableMatchers.failsWith;
import static org.junit.Assert.assertThat;

public class MiscTest {

    @Test
    public void testFirstOnEmpty() {
        assertThat(Observable.empty().first(), failsWith(NoSuchElementException.class));
    }

    @Test
    public void testLastOnEmpty() {
        assertThat(Observable.empty().last(), failsWith(NoSuchElementException.class));
    }

    @Test
    public void testTakeLast() {
        assertThat(Observable.just("grapefrukt").takeLast(5), emitsValue("grapefrukt"));
        assertThat(Observable.empty().takeLast(5), emitsNothing());
    }

}
