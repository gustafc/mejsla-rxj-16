package gustafc.ch02;

import fj.Ord;
import fj.data.Set;
import fj.data.Stream;
import rx.Observable;

import java.util.Collection;
import java.util.HashSet;

public class Eratosthenes {

    public static final int LAST_SIGNED_32_BIT_PRIME = 2147483647;

    private static Set<Integer> nextPrimeSet(Set<Integer> seen) {
        int greatestSeen = seen.max().some();
        return Stream.range(greatestSeen + 1, 1L + Integer.MAX_VALUE).filter(candidate -> {
            int max = (int) Math.ceil(Math.sqrt(greatestSeen));
            return seen.toStream()
                    .takeWhile(prime -> prime <= max)
                    .forall(prime -> candidate % prime != 0);
        }).map(seen::insert).head();
    }

    public static Observable<Integer> primes() {
        return Observable.just("whatever").repeat()
                .scan(Set.set(Ord.intOrd, 2), (primes, ignore) -> nextPrimeSet(primes))
                .map(primes -> primes.max().some())
                .takeUntil(n -> n == LAST_SIGNED_32_BIT_PRIME);
    }

    public static Observable<Integer> nonFp() {
        Collection<Integer> primes = new HashSet<>();
        return Observable.just(1).repeat().scan(2, (a, b) -> a + b).filter(integer -> {
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
        if (levels <= 1) return s;
        final int head = s.toBlocking().first();
        final Observable<Integer> tail = s.skip(1).filter(i -> i % head != 0);
        return kalleSieve(tail, levels - 1).startWith(head);
    }


}
