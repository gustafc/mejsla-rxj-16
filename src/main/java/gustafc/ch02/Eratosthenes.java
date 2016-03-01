package gustafc.ch02;

import rx.Observable;

public class Eratosthenes {

    public static final int LAST_SIGNED_32_BIT_PRIME = 2147483647;

    static class Sifter {

        private final Sifter previous;
        private final int prime;

        Sifter(Sifter previous, int n) {
            this.previous = previous;
            this.prime = n;
        }

        Sifter next() {
            int candidate = prime;
            while (!isPrime(++candidate))
                ;
            return new Sifter(this, candidate);
        }

        private boolean isPrime(int candidate) {
            for (Sifter current = this; current != null; current = current.previous) {
                if (candidate % current.prime == 0) return false;
            }
            return true;
        }
    }

    public static Observable<Integer> primes(){
        return Observable.just("whatever").repeat()
                .scan(new Sifter(null, 2), (sifter, ignore) -> sifter.next())
                .map(sifter -> sifter.prime)
                .takeUntil(n -> n == LAST_SIGNED_32_BIT_PRIME);
    }

}
