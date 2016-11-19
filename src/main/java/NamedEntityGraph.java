import entity_extractor.EntityExtractor;
import entity_extractor.OpenCalaisExtractor;
import entity_extractor.TextEntities;
import gr.demokritos.iit.jinsect.documentModel.comparators.NGramCachedGraphComparator;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentWordGraph;
import gr.demokritos.iit.jinsect.structs.GraphSimilarity;
import utils.Percentage;
import utils.VerySimpleFormatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.*;

public class NamedEntityGraph {
    private final Logger LOGGER = Logger.getLogger("NamedEntityGraph");
    private ArrayList<String> placeholders;
    private Handler consoleHandler = null;
    private Handler fileHandler = null;

    public static void main(String[] args) {
        NamedEntityGraph neg = new NamedEntityGraph();

        try {
            neg.start();
        } catch(IOException e) {
            System.err.println("Problem writing log file");
        }
    }

    public void start() throws IOException {
        // Setup logger
        LOGGER.setLevel(Level.FINEST);
        LOGGER.setUseParentHandlers(false);

        // Add handlers to the logger
        consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new VerySimpleFormatter());
        LOGGER.addHandler(consoleHandler);

        fileHandler = new FileHandler("./neg.log");
        fileHandler.setLevel(Level.FINEST);
        fileHandler.setFormatter(new VerySimpleFormatter());
        LOGGER.addHandler(fileHandler);

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
                LOGGER.log(Level.INFO, "working on all files in " + input.getAbsolutePath());
                File[] files = input.listFiles();
                if (files != null) {
                    int i = 1;
                    int totalFiles = files.length;
                    double percentage = 0;
                    double currPercent;

                    for (File file : files) {
                        if (file.isFile()) {
                            currPercent = Percentage.percent(i, totalFiles);
                            Level lvl = Level.FINE;
                            if (currPercent - percentage > 1) {
                                lvl = Level.INFO;
                                percentage = currPercent;
                            }

                            LOGGER.log(lvl, String.format("[main] (" + i + "/" + files.length + " - %.2f%%) Getting entities for " + file + "", currPercent));

                            TextEntities entities = entityExtractor.getEntities(file);
//                            entities.printEntities();

                            texts.add(entities);

                            LOGGER.log(Level.FINE, "[main] Got " + entities.getEntities().size() + " extracted entities from " + file + "\n");
                        } else {
                            LOGGER.log(Level.FINE, "Skipping " + file.getAbsolutePath());
                        }

                        i++;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getStackTrace().toString());
            LOGGER.log(Level.SEVERE, "Not comparing anything, there was an error");
            return;
        }

        // Compare every text with every other text
        LOGGER.log(Level.INFO, "Starting text comparisons...");

        int textsLen = texts.size();
        int totalComparisons = textsLen * (textsLen - 1) / 2;
        int comparisonsDone = 0;
        double percentage = 0;
        double currPercent;
        for (int i = 0; i < textsLen - 1; i++) {
            for (int j = i + 1; j < textsLen; j++) {
                currPercent = Percentage.percent(comparisonsDone, totalComparisons);
                Level lvl = Level.FINE;
                if (currPercent - percentage > 0.1) {
                    lvl = Level.INFO;
                    percentage = currPercent;
                }

                TextEntities text1 = texts.get(i);
                TextEntities text2 = texts.get(j);

                LOGGER.log(lvl, String.format("[%.2f%% - " + comparisonsDone + "/" + totalComparisons + "] Comparing " + text1.getTitle() + " with " + text2.getTitle(), percentage));

                try {
                    compareTexts(text1, text2);
                } catch (StackOverflowError e) {
                    errors.add(text1.getTitle() + " & " + text2.getTitle());
                }
                LOGGER.log(Level.FINE, "");
                comparisonsDone++;
            }
        }

        // Print any errors that occurred
        if (errors.size() > 0) {
            LOGGER.log(Level.SEVERE, "Errors:");

            for (String error : errors) {
                LOGGER.log(Level.SEVERE, error);
            }
        }
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

        LOGGER.log(Level.FINE, "N-gram similarity:\t" + sim.toString());

        // Compare with named entity graph placeholder method
        for (String placeholder : placeholders) {
            text1data = text1.getEntityTextWithPlaceholders(placeholder);
            text2data = text2.getEntityTextWithPlaceholders(placeholder);

            sim = getWordGraphSimilarity(comparator, text1data, text2data);

            LOGGER.log(Level.FINE, "Placeholder (" + placeholder + "):\t\t" + sim.toString());
        }

        // Compare with named entity graph placeholder same size method
        for (String placeholder : placeholders) {
            text1data = text1.getEntityTextWithPlaceholderSameSize(placeholder);
            text2data = text2.getEntityTextWithPlaceholderSameSize(placeholder);

            // Code that gives stack overflow exception:
            sim = getWordGraphSimilarity(comparator, text1data, text2data);

            LOGGER.log(Level.FINE, "PHSameSize (" + placeholder + "):\t\t" + sim.toString());
        }

        // Compare with named entity graph random word method
        text1data = text1.getEntityTextWithRandomWord();
        text2data = text2.getEntityTextWithRandomWord();

        sim = getWordGraphSimilarity(comparator, text1data, text2data);

        LOGGER.log(Level.FINE, "Random words:\t\t\t" + sim.toString());
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
