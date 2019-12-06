package com.rahat;

import javafx.util.Pair;
import opennlp.tools.stemmer.PorterStemmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvertedIndex {

    List<String> stopwords = new ArrayList<>();

    Map<String, List<Pair<String, Integer>>> index = new HashMap<>();

    PorterStemmer stemmer = new PorterStemmer();

    /**
     * Load Stopwords (separated line by line) into an ArrayList
     */
    private void setStopwords() {
        try {
            stopwords = Files.readAllLines(Paths.get("./stopwords.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an inverted index based on the specified list of files.
     *
     * @param files
     * @throws IOException
     */
    public void createIndex(File[] files) throws IOException {
        setStopwords();

        for (File document : files) {
            int position = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(document))) {
                String line;
                while ((line = br.readLine()) != null) {

                    // Strip HTML, JS
                    line = line.replaceAll("\\<.*?\\>", "").replaceAll("<script>(.*)</script>", "");

                    // Strip Punctuation, Convert Word to Lowercase, Split by Whitespace
                    String[] tokens = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
                    for (String word : tokens) {
                        position++;

                        // Skip if stopword or empty string
                        if (stopwords.contains(word) || word.trim().isEmpty()) continue;

                        // Stem word using Porter Stemmer
                        word = stemmer.stem(word);

                        // If the word is not in the index, add it
                        if (!index.containsKey(word))
                            index.put(word, new ArrayList<>());

                        // Add the posting to our postings list
                        Pair<String, Integer> posting = new Pair<>(document.getName(), position);
                        index.get(word).add(posting);
                    }
                }
            }
        }
    }
}
