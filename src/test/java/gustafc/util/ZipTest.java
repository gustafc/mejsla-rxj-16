package gustafc.util;

import org.junit.Test;
import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gustafc.testutil.ObservableMatchers.emitsNothing;
import static gustafc.testutil.ObservableMatchers.emitsValues;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ZipTest {

    @Test
    public void shouldWorkForEmptySequence() {
        assertThat(Zip.withIndex(Observable.empty()), emitsNothing());
    }

    @Test
    public void shouldGiveCorrectIndices() {
        assertThat(Zip.withIndex(Observable.just("apelsin", "banan", "citron", "durian")),
                emitsValues(
                        new Zip.Indexed<>(0, "apelsin"),
                        new Zip.Indexed<>(1, "banan"),
                        new Zip.Indexed<>(2, "citron"),
                        new Zip.Indexed<>(3, "durian")
                ));
    }

    @Test
    public void shouldGiveCorrectIndicesWithDoubleSubscribe() {
        Observable<Zip.Indexed<String>> zipped = Zip.withIndex(Observable.just("apelsin", "banan", "citron", "durian"));
        List<Zip.Indexed<String>> output = new ArrayList<>();
        zipped.toBlocking().forEach(output::add);
        zipped.cache().toBlocking().forEach(output::add);
        assertEquals(
                Arrays.asList(
                        new Zip.Indexed<>(0, "apelsin"),
                        new Zip.Indexed<>(1, "banan"),
                        new Zip.Indexed<>(2, "citron"),
                        new Zip.Indexed<>(3, "durian"),
                        new Zip.Indexed<>(0, "apelsin"),
                        new Zip.Indexed<>(1, "banan"),
                        new Zip.Indexed<>(2, "citron"),
                        new Zip.Indexed<>(3, "durian")
                ),
                output
        );
    }

}