package gustafc.ch03;

import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

public class ObservableFactoriesTest {

    @Test
    public void testCompletion() {
        assertEquals(Arrays.asList(
                "before create",
                "after create, before subscribe",
                "onNext 1",
                "onNext 2",
                "onComplete",
                "after subscribe - unsubscribed"
        ), run(Subscriber::onCompleted));
    }

    @Test
    public void testFailing() {
        assertEquals(Arrays.asList(
                "before create",
                "after create, before subscribe",
                "onNext 1",
                "onNext 2",
                "onError message=BOOM",
                "after subscribe - unsubscribed"
        ), run(subscriber -> subscriber.onError(new Exception("BOOM"))));
    }

    private List<String> run(Consumer<Subscriber<?>> finishingAction) {
        List<String> log = Collections.synchronizedList(new ArrayList<>());
        log.add("before create");
        Observable<String> obs = Observable.<String>create(subscriber -> {
            subscriber.onNext("onNext 1");
            subscriber.onNext("onNext 2");
            finishingAction.accept(subscriber);
            subscriber.onNext("ZALGO");
        });
        log.add("after create, before subscribe");
        Subscription subscription = obs.subscribe(
                log::add,
                e -> log.add("onError message=" + e.getMessage()),
                () -> log.add("onComplete")
        );
        log.add("after subscribe - " + (subscription.isUnsubscribed() ? "unsubscribed" : "subscribed"));
        return log;
    }

}
