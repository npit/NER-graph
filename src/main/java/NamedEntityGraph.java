import entity_extractor.EntityExtractor;
import entity_extractor.OpenCalaisExtractor;
import entity_extractor.TextEntities;
import gr.demokritos.iit.jinsect.documentModel.comparators.NGramCachedGraphComparator;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentWordGraph;
import gr.demokritos.iit.jinsect.structs.GraphSimilarity;

import java.io.File;
import java.util.ArrayList;

public class NamedEntityGraph {
    private static ArrayList<String> placeholders;

    public static void main(String[] args) {
        // Main variables
        String inputFolder = "texts/input";

        placeholders = new ArrayList<>();
        placeholders.add(".");
//        placeholders.add("");
//        placeholders.add("-");
//        placeholders.add("WOW");
        placeholders.add("A");

        File input = new File(inputFolder);
        EntityExtractor entityExtractor = new OpenCalaisExtractor();
        ArrayList<TextEntities> texts = new ArrayList<>();
        ArrayList<String> errors = new ArrayList<>();

        try {
            if (input.isDirectory()) {
                System.out.println("working on all files in " + input.getAbsolutePath());
                File[] files = input.listFiles();
                if (files != null) {
                    int i = 1;
                    int totalFiles = files.length;
                    double percentage;

                    for (File file : files) {
                        if (file.isFile()) {
                            percentage = (i * 100.0)/totalFiles;
                            System.out.format("[main] (" + i + "/" + files.length + " - %.2f%%) Getting entities for " + file + "\n", percentage);

                            TextEntities entities = entityExtractor.getEntities(file);
//                            entities.printEntities();

                            texts.add(entities);

                            System.out.println("[main] Got " + entities.getEntities().size() + " extracted entities from " + file + "\n");
                        } else {
                            System.out.println("Skipping " + file.getAbsolutePath());
                        }

                        i++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Not comparing anything, there was an error");
            return;
        }

        // Compare every text with every other text
        int textsLen = texts.size();
        for (int i = 0; i < textsLen - 1; i++) {
            for (int j = i + 1; j < textsLen; j++) {
                TextEntities text1 = texts.get(i);
                TextEntities text2 = texts.get(j);

                System.out.println("Comparing " + text1.getTitle() + " (" + text1.getEntities().size() + " entities) with " + text2.getTitle() + "(" + text2.getEntities().size() + " entities)");

                try {
                    compareTexts(text1, text2);
                } catch (StackOverflowError e) {
                    errors.add(text1.getTitle() + " (" + text1.getEntities().size() + " entities) & " + text2.getTitle() + "(" + text2.getEntities().size() + " entities)");
                }
                System.out.println();
            }
        }

        // Print any errors that occurred
        if (errors.size() > 0) {
            System.err.println("Errors:");

            for (String error : errors) {
                System.err.println(error);
            }
        }
    }

    /**
     * Compare texts in various ways
     * @param text1 Extracted data from 1st text
     * @param text2 Extracted data from 2nd text
     */
    private static void compareTexts(TextEntities text1, TextEntities text2) {
        // Create comparator object
        NGramCachedGraphComparator comparator = new NGramCachedGraphComparator();

        // Declare graph similarity and string objects
        GraphSimilarity sim;
        String text1data, text2data;

        // Compare with n-gram graphs
        DocumentNGramGraph nGramGraph1 = new DocumentWordGraph();
        DocumentNGramGraph nGramGraph2 = new DocumentWordGraph();
        nGramGraph1.setDataString(text1.getText());
        nGramGraph2.setDataString(text2.getText());

        sim = comparator.getSimilarityBetween(nGramGraph1, nGramGraph2);

        System.out.println("N-gram similarity:\t" + sim.toString());

        // Compare with named entity graph placeholder method
        for (String placeholder : placeholders) {
            text1data = text1.getEntityTextWithPlaceholders(placeholder);
            text2data = text2.getEntityTextWithPlaceholders(placeholder);

            sim = getWordGraphSimilarity(comparator, text1data, text2data);

            System.out.println("Placeholder (" + placeholder + "):\t" + sim.toString());
        }

        // Compare with named entity graph placeholder same size method
        for (String placeholder : placeholders) {
            text1data = text1.getEntityTextWithPlaceholderSameSize(placeholder);
            text2data = text2.getEntityTextWithPlaceholderSameSize(placeholder);

            // Code that gives stack overflow exception:
            sim = getWordGraphSimilarity(comparator, text1data, text2data);

            System.out.println("PHSameSize (" + placeholder + "):\t\t" + sim.toString());
        }

        // Compare with named entity graph random word method
        text1data = text1.getEntityTextWithRandomWord();
        text2data = text2.getEntityTextWithRandomWord();

        sim = getWordGraphSimilarity(comparator, text1data, text2data);

        System.out.println("Random words:\t\t" + sim.toString());

    }

    /**
     * Create document word graphs for each text and return their graph similarity using the comparator
     * @param comparator    Comparator to use
     * @param text1         First text
     * @param text2         Second text
     * @return              Graph similarity object
     */
    private static GraphSimilarity getWordGraphSimilarity(NGramCachedGraphComparator comparator, String text1, String text2) {
        // Create graph for text 1
        DocumentWordGraph wordGraph1 = new DocumentWordGraph();
        wordGraph1.setDataString(text1);

        // Create graph for text 2
        DocumentWordGraph wordGraph2 = new DocumentWordGraph();
        wordGraph2.setDataString(text2);

        return comparator.getSimilarityBetween(wordGraph1, wordGraph2);
    }
}
