package gustafc.util;

import java.util.Optional;

public class Cast {

    public static <T> Optional<T> tryCast(Class<T> cls, Object val) {
        return Optional.ofNullable(val).filter(cls::isInstance).map(cls::cast);
    }

}
