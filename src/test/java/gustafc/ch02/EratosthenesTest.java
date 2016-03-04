package gustafc.ch02;

import org.hamcrest.Matcher;
import org.junit.Test;
import rx.Observable;

import static gustafc.testutil.ObservableMatchers.emitsValues;
import static org.junit.Assert.assertThat;

public class EratosthenesTest {

    @Test
    public void testSieve() {
        assertThat(Eratosthenes.primes().take(25), emits25FirstPrimes());
    }

    @Test
    public void testNonFp() {
        assertThat(Eratosthenes.nonFp().take(25), emits25FirstPrimes());
    }

    private Matcher<Observable<? extends Integer>> emits25FirstPrimes() {
        return emitsValues(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97);
    }

}