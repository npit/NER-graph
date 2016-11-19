import entity_extractor.EntityExtractor;
import entity_extractor.TextEntities;
import entity_extractor.open_calais_extractor.OpenCalaisExtractor;
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

        try {
            if (input.isDirectory()) {
                System.out.println("working on all files in " + input.getAbsolutePath());
                File[] files = input.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            System.out.println("[main] Getting entities for " + file);

                            TextEntities entities = entityExtractor.getEntities(file);
//                            entities.printEntities();

                            texts.add(entities);

                            System.out.println("[main] Got " + entities.getEntities().size() + " extracted entities from " + file + "\n");
                        } else {
                            System.out.println("Skipping " + file.getAbsolutePath());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        compareTexts(texts.get(0), texts.get(1));
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
