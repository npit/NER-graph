import clustering.Cluster;
import clustering.ModifiedMarkovClusterer;
import clustering.TestableMarkovClusterer;
import entity_extractor.EntityExtractor;
import entity_extractor.OpenCalaisExtractor;
import entity_extractor.TextEntities;
import utils.Percentage;
import utils.VerySimpleFormatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.*;

/**
 * Created by Leo on 2016-12-10.
 */
@SuppressWarnings("SpellCheckingInspection")
public class MarkovClustererTest {
    private final Logger LOGGER = Logger.getLogger("MarkovClusterer");

    public static void main(String[] args) {
        MarkovClustererTest mct = new MarkovClustererTest();

        try {
            mct.start();
        } catch(IOException e) {
            System.err.println("Problem writing log file");
        }
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

        Handler fileHandler = new FileHandler("./neg_markov.log");
        fileHandler.setLevel(Level.FINEST);
        fileHandler.setFormatter(new VerySimpleFormatter());
        LOGGER.addHandler(fileHandler);

        // Main variables
        String inputFolder = "texts/input";

        File input = new File(inputFolder);
        EntityExtractor entityExtractor = new OpenCalaisExtractor();
        ArrayList<TextEntities> texts = new ArrayList<>();

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
                            // Log the progress so far
                            currPercent = Percentage.percent(i, totalFiles);
                            Level lvl = Level.FINE;
                            if (currPercent - percentage > 10.0 || i == totalFiles) {
                                lvl = Level.INFO;
                                percentage = currPercent;
                            }

                            LOGGER.log(lvl, String.format("[main] (" + i + "/" + files.length + " - %.2f%%) Getting entities for " + file + "", currPercent));

                            // Get entities for this file and save them
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
            LOGGER.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
            LOGGER.log(Level.SEVERE, "Not comparing anything, there was an error");
            return;
        }

        LOGGER.log(Level.INFO, "Starting the markov clustering");
//        MarkovClusterer mc = new MarkovClusterer(texts);
        TestableMarkovClusterer tmc;

        // Test multiple inflation factors
        double[] factors = new double[]{1.4, 2.0, 3.0, 4.0, 6.0};
        for (int i = 0; i < factors.length; i++) {
            double factor = factors[i];
            LOGGER.log(Level.INFO, "Markov Clustering with inflaction factor: " + factor);
            tmc = new TestableMarkovClusterer(texts, false, 5, factor);

            List<Cluster> clusters = tmc.calculateClusters();

            LOGGER.log(Level.INFO, "Number of clusters: " + clusters.size());

            for (Cluster c : clusters) {
                LOGGER.log(Level.INFO, c.getID() + " => " + c.size() + " texts");
            }
        }

        // Test multiple num of iterations
//        for (int i = 0; i < 10; i++) {
//            LOGGER.log(Level.INFO, "Markov Clustering with MODIFIED NORMALIZATION and: " + (i + 1) + " num of iterations");
//            tmc = new TestableMarkovClusterer(texts, true, i + 1, 3.0);
//
//            List<Cluster> clusters = tmc.calculateClusters();
//
//            LOGGER.log(Level.INFO, "Number of clusters: " + clusters.size());
//
//            for (Cluster c : clusters) {
//                LOGGER.log(Level.INFO, c.getID() + " => " + c.size() + " texts");
//            }
//        }
//        for (int i = 0; i < 10; i++) {
//            LOGGER.log(Level.INFO, "Markov Clustering with ORIGINAL NORMALIZATION and: " + (i + 1) + " num of iterations");
//            tmc = new TestableMarkovClusterer(texts, false, i + 1, 3.0);
//
//            List<Cluster> clusters = tmc.calculateClusters();
//
//            LOGGER.log(Level.INFO, "Number of clusters: " + clusters.size());
//
//            for (Cluster c : clusters) {
//                LOGGER.log(Level.INFO, c.getID() + " => " + c.size() + " texts");
//            }
//        }
//        ModifiedMarkovClusterer mc = new ModifiedMarkovClusterer(texts);
//
//        List<Cluster> clusters = mc.calculateClusters();
//
//        LOGGER.log(Level.INFO, "Number of clusters: " + clusters.size());
//
//        for (Cluster c : clusters) {
//            LOGGER.log(Level.INFO, c.getID() + " => " + c.size() + " texts");
//        }
    }

}
