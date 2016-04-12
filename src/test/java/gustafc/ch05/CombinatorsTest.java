package gustafc.ch05;

import org.junit.Test;
import rx.Observable;

import static gustafc.ch05.Combinators.emitPeriodically;
import static gustafc.testutil.ObservableMatchers.emitsValues;
import static org.junit.Assert.assertThat;

public class CombinatorsTest {

    @Test
    public void testCombineWithLatest() {
        Observable<String> fruits = emitPeriodically(10, "Apelsin", "Banan", "Citron");
        Observable<String> preparations =  emitPeriodically(15, "paj", "kaka").startWith("marmelad");
        assertThat(
                Observable.combineLatest(fruits, preparations, (fruit, preparation) -> fruit + preparation),
                emitsValues("Apelsinmarmelad", "Bananmarmelad", "Bananpaj", "Citronpaj", "Citronkaka"));
    }

}