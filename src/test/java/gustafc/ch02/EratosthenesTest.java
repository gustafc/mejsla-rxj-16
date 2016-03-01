package gustafc.ch02;

import org.junit.Test;

import static gustafc.testutil.ObservableMatchers.emitsValues;
import static org.junit.Assert.assertThat;

public class EratosthenesTest {

    @Test
    public void testSieve() {
        assertThat(Eratosthenes.primes().take(20),
                emitsValues(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71));
    }

}