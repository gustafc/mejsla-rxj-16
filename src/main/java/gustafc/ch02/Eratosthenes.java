package gustafc.ch02;

import fj.Ord;
import fj.data.Set;
import fj.data.Stream;
import rx.Observable;

import java.util.Collection;
import java.util.HashSet;

import static gustafc.util.ObservableFactories.generate;

public class Eratosthenes {

    public static final int LAST_SIGNED_32_BIT_PRIME = 2147483647;

    public static Observable<Integer> primes() {
        return generate(Set.set(Ord.intOrd, 2), seen -> {
            int greatestSeen = seen.max().some();
            return Stream.range(greatestSeen + 1, 1L + Integer.MAX_VALUE).filter(candidate -> {
                int max = (int) Math.ceil(Math.sqrt(greatestSeen));
                return seen.toStream()
                        .takeWhile(prime -> prime <= max)
                        .forall(prime -> candidate % prime != 0);
            }).map(seen::insert).head();
        }).map(primes -> primes.max().some()).takeUntil(n -> n == LAST_SIGNED_32_BIT_PRIME);
    }

    public static Observable<Integer> nonFp() {
        Collection<Integer> primes = new HashSet<>();
        return generate(2, a -> a + 1).filter(integer -> {
            boolean isPrime = nonFpIsPrime(integer, primes);
            if (isPrime) primes.add(integer);
            return isPrime;
        });
    }

    private static boolean nonFpIsPrime(Integer integer, Collection<Integer> primes) {
        return !primes.stream().anyMatch(prime -> integer % prime == 0);
    }

    public static Observable<Integer> kalle(int to) {
        return kalleSieve(Observable.range(2, to - 1), (int) Math.sqrt(to));
    }


    public static Observable<Integer> kalleSieve(Observable<Integer> s, int levels) {
        return levels <= 1 ? s : s.take(1).flatMap(head -> {
            Observable<Integer> tail = s.skip(1).filter(i -> i % head != 0);
            return kalleSieve(tail, levels - 1).startWith(head);
        });
    }


}
