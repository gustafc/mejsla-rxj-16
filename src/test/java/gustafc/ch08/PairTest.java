package gustafc.ch08;

import org.junit.Test;
import rx.Observable;

import static gustafc.testutil.ObservableMatchers.emitsNothing;
import static gustafc.testutil.ObservableMatchers.emitsValues;
import static org.junit.Assert.*;

public class PairTest {

    @Test
    public void shouldGiveNoPairsForEmptyObservable() {
        assertThat(Observable.empty().compose(Pair.pairs()), emitsNothing());
    }

    @Test
    public void shouldGiveNoPairsForSingleElementObservable() {
        assertThat(Observable.just("a").compose(Pair.pairs()), emitsNothing());
    }

    @Test
    public void shouldGiveCorrectPairs() {
        assertThat(Observable.just("a", "b", "c", "d").compose(Pair.pairs()), emitsValues(
                new Pair<>("a", "b"),
                new Pair<>("b", "c"),
                new Pair<>("c", "d")
        ));
    }

    @Test
    public void testPairMap() {
        assertThat(Observable.just("1", "2", "3", "4").compose(Pair.pairMap((a, b) -> Integer.parseInt(a + b))),
                emitsValues(12, 23, 34));
    }



}