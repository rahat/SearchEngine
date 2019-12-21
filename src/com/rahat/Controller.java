package com.rahat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import opennlp.tools.stemmer.PorterStemmer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class Controller {
    @FXML
    public HBox header;

    @FXML
    private TextField query;

    @FXML
    private WebView webView;

    private InvertedIndex invertedIndex;

    public void initialize() throws IOException {
        invertedIndex = new InvertedIndex();

        // Fetch Corpus Documents
        File[] files = new File("res/corpus/html/").listFiles((dir, name) -> name.endsWith(".html"));
        if (files != null) {
            System.out.println(files.length + " documents found.");
            invertedIndex.createIndex(files);
            System.out.println("Indexed " + invertedIndex.index.size() + " terms.");
        } else {
            throw new IOException("Unable to locate documents, make sure they are located in the right directory");
        }
        webView.getEngine().setUserStyleSheetLocation(getClass().getResource("style.css").toString());

        webView.setBlendMode(BlendMode.DARKEN);
        String start = "<p>To get started, enter a query and press Search. </p>" +
                " <p>Example: <p>When are the 2020 NBA finals?</p>" +
                " <p>How Much Battery Life Does the iPhone 11 Pro Have?</p>" +
                " <p>Who is hosting the 2020 Olympics?</p>";
        webView.getEngine().loadContent(start.toString(), "text/html");
    }

    public void Search(ActionEvent actionEvent) throws IOException {
        String search = query.getText();
        System.out.println("Query: " + search);

        DecimalFormat df = new DecimalFormat("#%"); // Format as Percentage

        Map<String, Integer> frequencyMap = new HashMap<>(); // Maps Document Name to Frequency

        // Strip Punctuation, Convert Word to Lowercase, Split by Whitespace
        String[] words = query.getText().replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().split("\\s+");
        PorterStemmer stemmer = new PorterStemmer();

        for (String w : words) {
            String stem = stemmer.stem(w);
            System.out.println("{" + w + "} -> {" + stem + "}");

            if (invertedIndex.index.containsKey(stem)) {
                System.out.println("Stem found in index.");

                // For each posting
                invertedIndex.index.get(stem).forEach((posting) -> {
                    String doc = posting.getKey();
                    if (!frequencyMap.containsKey(doc)) {
                        frequencyMap.put(doc, 1);
                    } else {
                        int occurrences = frequencyMap.get(doc);
                        frequencyMap.put(doc, occurrences + 1);
                    }
                });
            }
        }

        // Sort by Frequency (Descending Order)
        List<Map.Entry<String, Integer>> list = new ArrayList<>(frequencyMap.entrySet());
        Comparator<Map.Entry<String, Integer>> sortByFreq = Map.Entry.comparingByValue();
        list.sort(sortByFreq.reversed());

        // Stripping Infrequent Values
        if (!list.isEmpty() && list.get(0).getValue() > 15) {
            list.removeIf(a -> a.getValue() < 15);
        }

        // Display Results
        StringBuilder results = new StringBuilder();
        if (list.isEmpty()) results = new StringBuilder("No results found...");

        for (Map.Entry<String, Integer> entry : list) {
            // Extract Document Information
            File file = new File("res/corpus/html/" + entry.getKey());
            Document doc = Jsoup.parse(file, "UTF-8", "");
            String link = "<a href=\"" + "res/corpus/html/" + entry.getKey() + "\">" + doc.title() + "</a>";
            results.append("<h3>").append(link).append("</h3>");

            // Get Snippet of Document
            String description = doc.body().text().substring(0, Math.min(doc.body().text().length(), 500));
            if (!description.substring(description.length() - 1).trim().isEmpty()) {
                description += "...";
            }
            results.append("<p>").append(description).append("</p>");
        }

        // Render Content
        webView.getEngine().loadContent(results.toString(), "text/html");
    }
}
