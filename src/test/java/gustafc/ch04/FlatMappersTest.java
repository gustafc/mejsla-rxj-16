package gustafc.ch04;

import org.junit.Test;
import rx.Observable;

import static gustafc.testutil.ObservableMatchers.emitsValues;
import static org.junit.Assert.*;

public class FlatMappersTest {

    @Test
    public void testCharsByIndex() {
        assertThat(FlatMappers.charsByIndex(Observable.just("ek", "bok")), emitsValues(
                "ek[0]=e",
                "ek[1]=k",
                "bok[0]=b",
                "bok[1]=o",
                "bok[2]=k"
        ));
    }
    @Test
    public void testCharsByIndexWithWithNestedMap() {
        assertThat(FlatMappers.charsByIndexWithNestedMap(Observable.just("ek", "bok")), emitsValues(
                "ek[0]=e",
                "ek[1]=k",
                "bok[0]=b",
                "bok[1]=o",
                "bok[2]=k"
        ));
    }

}