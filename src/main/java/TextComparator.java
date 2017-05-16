import csv_export.CSVExporter;
import csv_export.ComparisonContainer;
import entity_extractor.*;
import gr.demokritos.iit.conceptualIndex.structs.Distribution;
import utils.Methods;
import utils.Percentage;
import utils.VerySimpleFormatter;
import utils.tf_idf.DocumentOptimizedParser;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

/**
 * Main program.
 * Reads a folder of text files, gets entities for each using the specified entity extractor, the compares all texts
 * with all other texts with all the available methods, and outputs the results to a CSV file (and a log file)
 */
public class TextComparator {
    private final static boolean cacheGraphs = true;    // Enable/disable caching of the graphs in memory for speed, but can use a lot of RAM
    private final boolean keepTopTerms;   // If true, will leave top terms (ranked by TF-IDF) in the text when making the graphs

    private final Logger LOGGER = Logger.getLogger("NamedEntityGraph");

    public TextComparator() {
        // Enable TF-IDF if placeholder method or Cosine Similarity is enabled
        this.keepTopTerms = Methods.isEnabled(Methods.PLACEHOLDER) || Methods.isEnabled(Methods.COSINE);
    }

    public static void main(String[] args) {
        // Initialize the static hashmap
        new Methods();

        long totalTimeStart = System.currentTimeMillis();
        TextComparator neg = new TextComparator();

        try {
            neg.start();
        } catch (IOException e) {
            System.err.println("Problem writing log file");
        }

        long totalTimeEnd = System.currentTimeMillis();
        System.out.println("Total time: " + ((totalTimeEnd - totalTimeStart) / 1000.0) + " seconds");
    }

    private void start() throws IOException {
        // Setup logger
        LOGGER.setLevel(Level.FINEST);
        LOGGER.setUseParentHandlers(false);

        // Add handlers to the logger
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new VerySimpleFormatter());
        LOGGER.addHandler(consoleHandler);

        Handler fileHandler = new FileHandler("./neg.log");
        fileHandler.setLevel(Level.FINEST);
        fileHandler.setFormatter(new VerySimpleFormatter());
        LOGGER.addHandler(fileHandler);

        // Main variables
        String inputFolder = "texts/input";

        ArrayList<String> placeholders = new ArrayList<>();
//        placeholders.add(".");
//        placeholders.add("");
//        placeholders.add("-");
        placeholders.add("A");

        File input = new File(inputFolder);
        EntityExtractor entityExtractor = new OpenCalaisExtractor();
        ArrayList<TextEntities> texts = new ArrayList<>();
        Map<String, GraphCache> graphs = new HashMap<>();
        ArrayList<String> errors = new ArrayList<>();

        try {
            if (input.isDirectory()) {
                LOGGER.log(Level.INFO, "Working on all files in " + input.getAbsolutePath());

                // Get text entities and create graphs (if we should cache them)
                LOGGER.log(Level.INFO, "Getting text entities...");
                File[] files = input.listFiles();
                if (files != null) {
                    int i = 1;
                    int totalFiles = files.length;
                    double percentage = 0;
                    double currPercent;

                    for (File file : files) {
                        if (file.isFile()) {
                            // Log the progress so far
                            currPercent = Percentage.percent(i, totalFiles);
                            Level lvl = Level.FINE;
                            if (currPercent - percentage > 10 || i == totalFiles) {
                                lvl = Level.INFO;
                                percentage = currPercent;
                            }

                            LOGGER.log(lvl, String.format("[main] (" + i + "/" + files.length + " - %.2f%%) Getting entities for " + file + "", currPercent));

                            // Get entities for this file and save them
                            TextEntities entities = entityExtractor.getEntities(file);
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
            LOGGER.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
            LOGGER.log(Level.SEVERE, "Not comparing anything, there was an error");
            return;
        }

        // Calculate TF-IDF of documents, so we can keep top terms
        LOGGER.log(Level.INFO, "Calculating TF-IDF...");
        long tfIdfStart = System.currentTimeMillis();
        DocumentOptimizedParser dp = new DocumentOptimizedParser();
        if (keepTopTerms) {
            dp.parseFiles(texts);
        }

        // For Cosine Similarity, create text distrubutions containing all terms
        Map<String, double[]> fullDistributions = null;
        if (Methods.isEnabled(Methods.COSINE)) {
            Set<String> allTerms = dp.getAllTerms();
            ArrayList<String> allTermsList = new ArrayList<>();
            allTermsList.addAll(allTerms);
            fullDistributions = new HashMap<>();

            for (TextEntities text : texts) {
                double[] fullTextDistrib = new double[allTermsList.size()];
                Distribution<String> textDistrib = dp.getDistributionOfDocument(text.getTitle());

                int count = 0;
                for (String term : allTerms) {
                    // If the text contains this term use its value, else set it to 0
                    if (textDistrib.asTreeMap().containsKey(term)) {
                        fullTextDistrib[count] = textDistrib.getValue(term);
                    } else {
                        fullTextDistrib[count] = 0.0;
                    }

                    count++;
                }

                fullDistributions.put(text.getTitle(), fullTextDistrib);
            }
        }
        long tfIdfEnd = System.currentTimeMillis();

        // Calculate graphs in advance
        LOGGER.log(Level.INFO, "Calculating graphs...");
        long graphCalculationStart = System.currentTimeMillis();
        if (cacheGraphs) {
            for (TextEntities entities : texts) {
                GraphCache cache = new GraphCache(entities, dp);
                cache.calculateGraphs(placeholders);
                graphs.put(entities.getTitle(), cache);
            }
        }
        long graphCalculationEnd = System.currentTimeMillis();

        // Compare every text with every other text
        LOGGER.log(Level.INFO, "Starting text comparisons...");
        long comparisonsStart = System.currentTimeMillis();

        // List to keep all comparisons that were made to write them to CSV file
        List<ComparisonContainer> comparisons = Collections.synchronizedList(new ArrayList<ComparisonContainer>());

        int textsLen = texts.size();
        int cores = Runtime.getRuntime().availableProcessors();
        LOGGER.log(Level.INFO, "Using " + cores + " cores...");

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cores);

        // Start a thread for each CPU core
        for (int i = 0; i < cores; i++) {
            ComparisonWorker r = new ComparisonWorker(i, cores, textsLen, placeholders, errors, texts, graphs, comparisons, fullDistributions);
            executor.execute(r);
        }

        // Wait for threads to finish before continuing
        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long comparisonsEnd = System.currentTimeMillis();

        // Print any errors that occurred
        if (errors.size() > 0) {
            LOGGER.log(Level.SEVERE, "Errors:");

            for (String error : errors) {
                LOGGER.log(Level.SEVERE, error);
            }
        }

        // Export to CSV
        CSVExporter.exportCSV("out.csv", placeholders, comparisons);

        System.out.println("TF-IDF time: " + ((tfIdfEnd - tfIdfStart) / 1000.0) + " seconds");
        System.out.println("Graph creation time: " + ((graphCalculationEnd - graphCalculationStart) / 1000.0) + " seconds");
        System.out.println("Comparisons time: " + ((comparisonsEnd - comparisonsStart) / 1000.0) + " seconds");
    }
}
