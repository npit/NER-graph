package entity_extractor;

import gr.demokritos.iit.jinsect.documentModel.comparators.NGramCachedGraphComparator;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentWordGraph;
import gr.demokritos.iit.jinsect.structs.GraphSimilarity;
import utils.Percentage;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyRunnable implements Runnable {
    private final Logger LOGGER = Logger.getLogger("NamedEntityGraph");

    private final ArrayList<String> errors;
    private final int id;
    private final int cores;
    private final int textsLen;
    private final ArrayList<String> placeholders;
    private final ArrayList<TextEntities> texts;
    private String myLog;

    public MyRunnable(int id, int cores, int textsLen, ArrayList<String> placeholders, ArrayList<String> errors, ArrayList<TextEntities> texts) {
        this.id = id;
        this.cores = cores;
        this.textsLen = textsLen;
        this.placeholders = placeholders;
        this.errors = errors;
        this.texts = texts;
    }

    @Override
    public void run() {
        ArrayList<Integer> compGroups = new ArrayList<>();
        int comparisonsToDo = 0;

        // Find which comparisons to do
        for (int i = id; i < textsLen - 1; i++) {
            if (i % cores == id) {
                comparisonsToDo += textsLen - i - 1;
                compGroups.add(i);
            }
        }

        LOGGER.log(Level.INFO, "[Worker " + id + "] Going to do " + comparisonsToDo + " text comparisons");

        int comparisonsDone = 0;
        for (Integer i : compGroups) {
            // Do the comparisons for this i
            for (int j = i + 1; j < textsLen; j++) {
                myLog = "";

                TextEntities text1 = texts.get(i);
                TextEntities text2 = texts.get(j);

                myLog += "Comparing " + text1.getTitle() + " with " + text2.getTitle() + "\n";

                try {
                    compareTexts(text1, text2);
                } catch (StackOverflowError e) {
                    synchronized (errors) {
                        errors.add(text1.getTitle() + " & " + text2.getTitle());
                    }
                }
                LOGGER.log(Level.FINE, myLog);
            }

            // Print progress
            comparisonsDone += textsLen - i - 1;
            LOGGER.log(Level.INFO, String.format("[Worker " + id + "] Progress: %.3f%%", Percentage.percent(comparisonsDone, comparisonsToDo)));
        }

        LOGGER.log(Level.INFO, "[Worker " + id + "] Finished");
    }

    /**
     * Compare texts in various ways
     * @param text1 Extracted data from 1st text
     * @param text2 Extracted data from 2nd text
     */
    private void compareTexts(TextEntities text1, TextEntities text2) {
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

        myLog += "N-gram similarity:\t" + sim.toString() + "\n";

        // Compare with named entity graph placeholder method
        for (String placeholder : placeholders) {
            text1data = text1.getEntityTextWithPlaceholders(placeholder);
            text2data = text2.getEntityTextWithPlaceholders(placeholder);

            sim = getWordGraphSimilarity(comparator, text1data, text2data);

            myLog += "Placeholder (" + placeholder + "):\t" + sim.toString() + "\n";
        }

        // Compare with named entity graph placeholder same size method
        for (String placeholder : placeholders) {
            text1data = text1.getEntityTextWithPlaceholderSameSize(placeholder);
            text2data = text2.getEntityTextWithPlaceholderSameSize(placeholder);

            // Code that gives stack overflow exception:
            sim = getWordGraphSimilarity(comparator, text1data, text2data);

            myLog += "PHSameSize (" + placeholder + "):\t\t" + sim.toString() + "\n";
        }

        // Compare with named entity graph random word method
//        text1data = text1.getEntityTextWithRandomWord();
//        text2data = text2.getEntityTextWithRandomWord();
//
//        sim = getWordGraphSimilarity(comparator, text1data, text2data);
//
//        myLog += "Random words:\t\t\t" + sim.toString() + "\n";
    }

    /**
     * Create document word graphs for each text and return their graph similarity using the comparator
     * @param comparator    Comparator to use
     * @param text1         First text
     * @param text2         Second text
     * @return              Graph similarity object
     */
    private GraphSimilarity getWordGraphSimilarity(NGramCachedGraphComparator comparator, String text1, String text2) {
        // Create graph for text 1
        DocumentWordGraph wordGraph1 = new DocumentWordGraph();
        wordGraph1.setDataString(text1);

        // Create graph for text 2
        DocumentWordGraph wordGraph2 = new DocumentWordGraph();
        wordGraph2.setDataString(text2);

        return comparator.getSimilarityBetween(wordGraph1, wordGraph2);
    }
}
