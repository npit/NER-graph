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
    private static ArrayList<String> dummyWords;

    public static void main(String[] args) {
        // Main variables
        String inputFolder = "texts/input";

        dummyWords = new ArrayList<>();
        dummyWords.add(".");
//        dummyWords.add("");
//        dummyWords.add("-");
//        dummyWords.add("WOW");
        dummyWords.add("A");

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

        // Compare with n-gram graphs
        DocumentNGramGraph nGramGraph1 = new DocumentWordGraph();
        nGramGraph1.setDataString(text1.getText());

        DocumentNGramGraph nGramGraph2 = new DocumentWordGraph();
        nGramGraph2.setDataString(text2.getText());

        GraphSimilarity nGramSimilarity = comparator.getSimilarityBetween(nGramGraph1, nGramGraph2);

        System.out.println("N-gram similarity:\t" + nGramSimilarity.toString());

        // Compare with named entity graph dummy method
        for (String dummyWord : dummyWords) {
            String text1Dummy = text1.getEntityTextWithDummyWord(dummyWord);
            String text2Dummy = text2.getEntityTextWithDummyWord(dummyWord);

            GraphSimilarity entitySimilarity = getWordGraphSimilarity(comparator, text1Dummy, text2Dummy);

            System.out.println("Dummy (" + dummyWord + "):\t\t\t" + entitySimilarity.toString());
        }

        // Compare with named entity graph dummy same size method
        for (String dummyWord : dummyWords) {
            String text1Dummy = text1.getEntityTextWithDummyWordSameSize(dummyWord);
            String text2Dummy = text2.getEntityTextWithDummyWordSameSize(dummyWord);

            // Code that gives stack overflow exception:
            GraphSimilarity entitySimilarity = getWordGraphSimilarity(comparator, text1Dummy, text2Dummy);

            System.out.println("DummySameSize (" + dummyWord + "):\t" + entitySimilarity.toString());

        }
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
