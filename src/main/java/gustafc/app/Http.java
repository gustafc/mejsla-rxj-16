package gustafc.app;

import org.apache.http.Header;
import org.apache.http.nio.client.HttpAsyncClient;
import rx.Observable;
import rx.apache.http.ObservableHttp;
import rx.apache.http.ObservableHttpResponse;

import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

public class Http {

    private Http() {
        throw new AssertionError("Uninstantiable");
    }

    public static Observable<String> get(HttpAsyncClient httpClient, String url) {
        ObservableHttp<ObservableHttpResponse> get = ObservableHttp.createGet(url, httpClient);
        return get.toObservable()
                .flatMap(ObservableHttpResponse::getContent)
                .toList()
                .map(bs -> bs.stream().map(b -> new String(b, UTF_8)).collect(joining("\r\n")));
    }

}
