package gustafc.ch02;

import org.junit.Test;
import rx.Observable;

import static gustafc.testutil.ObservableMatchers.emitsNothing;
import static gustafc.testutil.ObservableMatchers.emitsValues;
import static org.junit.Assert.assertThat;

public class CalculatorTest {


    private Observable<String> calculatorWithInput(String... cmds) {
        return Calculator.run(Observable.from(cmds));
    }

    @Test
    public void shouldBeSilentWithoutFormulae() {
        assertThat(calculatorWithInput("a:1", "b:2", "c:3"), emitsNothing());
    }

    @Test
    public void shouldAddCorrectly() {
        assertThat(calculatorWithInput("a:1", "b:2", "=a+b", "a:3"), emitsValues("a+b=3.0", "a+b=5.0"));
    }

    @Test
    public void shouldSubtractCorrectly() {
        assertThat(calculatorWithInput("a:1", "b:2", "=a-b", "a:3"), emitsValues("a-b=-1.0", "a-b=1.0"));
    }

    @Test
    public void shouldMultiplyCorrectly() {
        assertThat(calculatorWithInput("a:1", "b:2", "=a*b", "a:3"), emitsValues("a*b=2.0", "a*b=6.0"));
    }

    @Test
    public void shouldDivideCorrectly() {
        assertThat(calculatorWithInput("a:1", "b:2", "=a/b", "a:4"), emitsValues("a/b=0.5", "a/b=2.0"));
    }

    @Test
    public void shouldDivideByZero() {
        assertThat(calculatorWithInput("a:1", "b:0", "=a/b"), emitsValues("a/b=Infinity"));
    }

    @Test
    public void shouldShowUnknownValues() {
        assertThat(calculatorWithInput("=a+b", "a:1", "c:3", "b:1"), emitsValues("a+b=?+?", "a+b=1.0+?", "a+b=2.0"));
    }

    @Test
    public void shouldNotShowDuplicates() {
        assertThat(calculatorWithInput("=a+a", "a:1"), emitsValues("a+a=?+?", "a+a=2.0"));
    }

}