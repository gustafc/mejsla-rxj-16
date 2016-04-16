package gustafc.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class Urls {

    private Urls() {
        throw new AssertionError("uninstantiable");
    }

    public static String encode(String s) {
        final String UTF_8 = StandardCharsets.UTF_8.name();
        try {
            return URLEncoder.encode(s, UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("Standard charset mysteriously not supported: " + UTF_8, e);
        }
    }
}
