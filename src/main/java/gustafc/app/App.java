package gustafc.app;

import gustafc.util.Urls;
import gustafc.util.Zip;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import org.json.JSONObject;
import org.json.JSONTokener;
import rx.Observable;
import rx.apache.http.ObservableHttp;
import rx.apache.http.ObservableHttpResponse;
import rx.observables.SwingObservable;
import rx.schedulers.SwingScheduler;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static gustafc.util.Cast.tryCast;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javax.swing.SwingUtilities.invokeLater;

public class App extends JFrame {

    App() {
        super("WP Searcher");
        CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
        httpClient.start();
        JPanel main = new JPanel(new BorderLayout()),
                nav = new JPanel(new BorderLayout());
        nav.setPreferredSize(new Dimension(300, 0));
        SearchField searchField = new SearchField();
        nav.add(searchField, BorderLayout.NORTH);
        SearchResultsPanel searchResultsPanel = new SearchResultsPanel(httpClient, searchField.searches);
        nav.add(searchResultsPanel, BorderLayout.CENTER);
        main.add(nav, BorderLayout.WEST);
        main.setPreferredSize(new Dimension(800, 800));
        main.add(new ArticleDisplay(httpClient, searchResultsPanel.selectedArticles), BorderLayout.CENTER);
        setContentPane(main);
        pack();
    }

    public static void main(String[] args) throws IOException {
        invokeLater(() -> {
            App app = new App();
            app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            app.setVisible(true);
        });
    }

}

final class SearchResult {
    final String title;
    final URI fullUri;

    SearchResult(String title, URI fullUri) {
        this.title = title;
        this.fullUri = fullUri;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, fullUri);
    }

    @Override
    public boolean equals(Object obj) {
        return tryCast(getClass(), obj)
                .map(that -> Objects.equals(this.title, that.title) && Objects.equals(this.fullUri, that.fullUri))
                .orElse(false);
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "title='" + title + '\'' +
                ", fullUri='" + fullUri + '\'' +
                '}';
    }
}

class SearchField extends JPanel {
    final Observable<String> searches;

    public SearchField() {
        super(new BorderLayout());
        add(new JLabel(new String(new int[]{128270}, 0, 1)), BorderLayout.WEST);
        JTextField searchField = new JTextField();
        add(searchField, BorderLayout.CENTER);
        this.searches = SwingObservable.fromDocumentEvents(searchField.getDocument())
                .map(ignore -> searchField.getText());
    }
}

class SearchResultsPanel extends JPanel {

    final Observable<URI> selectedArticles;

    private final DefaultListModel<SearchResult> items;

    public SearchResultsPanel(HttpAsyncClient httpClient, Observable<String> searchStrings) {
        super(new BorderLayout());
        items = new DefaultListModel<>();
        items.addElement(new SearchResult("Apelsin", URI.create("http://sv.wikpedia.org/wiki/Apelsin")));
        items.addElement(new SearchResult("Banan", URI.create("http://sv.wikpedia.org/wiki/Banan")));
        JList<SearchResult> list = new JList<>(items);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, ((SearchResult) value).title, index, isSelected, cellHasFocus);
            }
        });
        add(list, BorderLayout.CENTER);
        searchStrings
                .map(s -> s.trim().replaceAll("\\s+", " "))
                .debounce(200, MILLISECONDS)
                .distinctUntilChanged()
                .switchMap(query -> doSearch(query, httpClient))
                .observeOn(SwingScheduler.getInstance())
                .subscribe(this::receiveSearchResult);
        ListSelectionModel selectionModel = list.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectedArticles = SwingObservable.fromListSelectionEvents(selectionModel)
                .observeOn(SwingScheduler.getInstance())
                .map(evt -> list.getSelectedValue())
                .filter(r -> r != null)
                .map(r -> r.fullUri);
    }

    private Observable<List<SearchResult>> doSearch(String query, HttpAsyncClient httpClient) {
        if (query.isEmpty()) return Observable.just(emptyList());
        String url = "https://sv.wikipedia.org/w/api.php?action=query&list=search&srsearch="
                + Urls.encode(query) + "&format=json&utf8=";
        System.out.println("Load " + url);
        return Http.get(httpClient, url).map(s -> {
            System.out.println("Received " + s + " from " + url);
            JSONObject jsonObject = new JSONObject(new JSONTokener(s));
            return Collections.singletonList(new SearchResult(jsonObject.toString(), URI.create("about:blank")));
        });
    }

    private void receiveSearchResult(List<SearchResult> searchResults) {
        items.clear();
        searchResults.forEach(items::addElement);
    }
}

class ArticleDisplay extends JPanel {
    private final CloseableHttpAsyncClient httpClient;

    public ArticleDisplay(CloseableHttpAsyncClient httpClient, Observable<URI> selectedArticles) {
        super(new BorderLayout());
        this.httpClient = httpClient;
        JEditorPane editorPane = new JEditorPane("text/html", "<html><h1>APELSIN<p>Apelsin är en smarrig frukt.");
        add(editorPane, BorderLayout.CENTER);
        selectedArticles
                .switchMap(this::doLookup)
                .observeOn(SwingScheduler.getInstance())
                .subscribe(editorPane::setText);
    }

    private Observable<String> doLookup(URI uri) {
        return Http.get(httpClient, uri.toASCIIString());
    }
}