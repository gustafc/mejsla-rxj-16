package gustafc.util;

import org.junit.Test;
import rx.Observable;

import static gustafc.testutil.ObservableMatchers.emitsNothing;
import static gustafc.testutil.ObservableMatchers.emitsValues;
import static org.junit.Assert.*;

public class ZipTest {

    @Test
    public void shouldWorkForEmptySequence(){
        assertThat(Zip.withIndex(Observable.empty()), emitsNothing());
    }

    @Test
    public void shouldGiveCorrectIndices(){
        assertThat(Zip.withIndex(Observable.just("apelsin", "banan", "citron", "durian")),
                emitsValues(
                        new Zip.Indexed<>(0, "apelsin"),
                        new Zip.Indexed<>(1, "banan"),
                        new Zip.Indexed<>(2, "citron"),
                        new Zip.Indexed<>(3, "durian")
                ));
    }

}