package gustafc.app;

import gustafc.util.Urls;
import rx.Observable;
import rx.observables.SwingObservable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static gustafc.util.Cast.tryCast;
import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;
import static javax.swing.SwingUtilities.invokeLater;

public class App extends JFrame {

    private final SearchField searchField;
    private final SearchResultsPanel searchResultsPanel;
    private final ArticleDisplay articleDisplay;

    App() {
        super("WP Searcher");
        JPanel main = new JPanel(new BorderLayout()),
                nav = new JPanel(new BorderLayout());
        nav.setPreferredSize(new Dimension(400, 0));
        nav.add(searchField = new SearchField(), BorderLayout.NORTH);
        nav.add(searchResultsPanel = new SearchResultsPanel(searchField.searches), BorderLayout.CENTER);
        main.add(nav, BorderLayout.WEST);
        main.add(articleDisplay = new ArticleDisplay(searchResultsPanel.selectedArticles), BorderLayout.CENTER);
        setContentPane(main);
        pack();
    }

    public static void main(String[] args) {
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

    public SearchResultsPanel(Observable<String> searchStrings) {
        super(new BorderLayout());
        items = new DefaultListModel<>();
        items.addElement(new SearchResult("Apelsin", URI.create("frukt:apelsin")));
        items.addElement(new SearchResult("Banan", URI.create("frukt:banan")));
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
                .switchMap(this::doSearch)
                // TODO: Kör på EDT
                .subscribe(query -> SwingUtilities.invokeLater(() -> this.receiveSearchResult(query)));
        ListSelectionModel selectionModel = list.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectedArticles = SwingObservable.fromListSelectionEvents(selectionModel)
                // TODO: Kör på EDT
                .map(evt -> list.getSelectedValue())
                .filter(r -> r != null)
                .map(r -> r.fullUri);
    }

    private Observable<List<SearchResult>> doSearch(String query) {
        if (query.isEmpty()) return Observable.just(emptyList());
        return Observable.just(Arrays.stream(query.split("\\s+"))
                .map(q -> new SearchResult(q, URI.create("bogus:" + Urls.encode(q))))
                .collect(toList()));
    }

    private void receiveSearchResult(List<SearchResult> searchResults) {
        items.clear();
        searchResults.forEach(items::addElement);
    }
}

class ArticleDisplay extends JPanel {
    public ArticleDisplay(Observable<URI> selectedArticles) {
        super(new BorderLayout());
        JEditorPane editorPane = new JEditorPane("text/html", "<html><h1>APELSIN<p>Apelsin är en smarrig frukt.");
        add(editorPane, BorderLayout.CENTER);
        selectedArticles
                .switchMap(this::doLookup)
                // TODO: Kör på EDT
                .subscribe(editorPane::setText);
    }

    private Observable<String> doLookup(URI uri) {
        return Observable.just(String.format("<html><h1>%s<p> TODO: Hämta %s", uri, uri));
    }
}