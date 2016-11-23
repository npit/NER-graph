package entity_extractor;

import gr.demokritos.iit.jinsect.documentModel.comparators.NGramCachedGraphComparator;
import gr.demokritos.iit.jinsect.structs.GraphSimilarity;
import utils.Percentage;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComparisonWorker implements Runnable {
    private final Logger LOGGER = Logger.getLogger("NamedEntityGraph");

    private final ArrayList<String> errors;
    private final int id;
    private final int cores;
    private final int textsLen;
    private final ArrayList<String> placeholders;
    private final ArrayList<TextEntities> texts;
    private final Map<String, GraphCache> cacheMap;
    private String myLog;

    public ComparisonWorker(int id, int cores, int textsLen, ArrayList<String> placeholders, ArrayList<String> errors, ArrayList<TextEntities> texts, Map<String, GraphCache> cacheMap) {
        this.id = id;
        this.cores = cores;
        this.textsLen = textsLen;
        this.placeholders = placeholders;
        this.errors = errors;
        this.texts = texts;
        this.cacheMap = cacheMap;
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
                    compareTexts(text1.getTitle(), text2.getTitle());
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
     * @param title1    Title of first text to compare
     * @param title2    Title fo second text to compare
     */
    private void compareTexts(String title1, String title2) {
        // Create comparator and graph similarity objects
        NGramCachedGraphComparator comparator = new NGramCachedGraphComparator();
        GraphSimilarity sim;

        GraphCache text1Graphs = cacheMap.get(title1);
        GraphCache text2Graphs = cacheMap.get(title2);

        // Compare normal texts with n-gram graphs
        sim = comparator.getSimilarityBetween(text1Graphs.getnGramNormalText(), text2Graphs.getnGramNormalText());
        myLog += "N-gram similarity:\t" + sim.toString() + "\n";

        // Compare normal texts with word graphs
        sim = comparator.getSimilarityBetween(text1Graphs.getWordGraphNormalText(), text2Graphs.getWordGraphNormalText());
        myLog += "WordGraph similarity:\t" + sim.toString() + "\n";

        // Compare with named entity graph placeholder method
        for (String ph : placeholders) {
            sim = comparator.getSimilarityBetween(text1Graphs.getWordGraphPH(ph), text2Graphs.getWordGraphPH(ph));
            myLog += "Placeholder (" + ph + "):\t" + sim.toString() + "\n";
        }

        // Compare with named entity graph placeholder same size method
        for (String ph : placeholders) {
            sim = comparator.getSimilarityBetween(text1Graphs.getWordGraphPHSS(ph), text2Graphs.getWordGraphPHSS(ph));
            myLog += "PHSameSize (" + ph + "):\t\t" + sim.toString() + "\n";
        }

        // Compare with named entity graph random word method
        sim = comparator.getSimilarityBetween(text1Graphs.getWordGraphRand(), text2Graphs.getWordGraphRand());
        myLog += "Random words:\t\t\t" + sim.toString() + "\n";
    }
}
