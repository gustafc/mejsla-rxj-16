package gustafc.ch04;

import gustafc.util.Zip;
import rx.Observable;

import java.util.Iterator;

public class FlatMappers {

    public static Observable<String> charsByIndex(Observable<String> strings){
        return strings.flatMap(
                s -> Zip.withIndex(charsIn(s)),
                (s, z) -> String.format("%s[%d]=%s", s, z.index, z.value)
        );
    }

    public static Observable<String> charsByIndexWithNestedMap(Observable<String> strings){
        return strings.flatMap(
                s -> Zip.withIndex(charsIn(s))
                        .map(z -> String.format("%s[%d]=%s", s, z.index, z.value))
        );
    }

    private static Observable<Character> charsIn(final String s) {
        return Observable.from(() -> new Iterator<Character>() {
            int idx = 0;
            @Override
            public boolean hasNext() {
                return idx < s.length();
            }

            @Override
            public Character next() {
                return s.charAt(idx++);
            }
        });
    }

}
