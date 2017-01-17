import Jama.Matrix;
import entity_extractor.EntityExtractor;
import entity_extractor.OpenCalaisExtractor;
import entity_extractor.TextEntities;
import utils.Percentage;
import utils.VerySimpleFormatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.*;

/**
 * Creates a matrix which shows for each pair of texts how many common entities they have
 */
@SuppressWarnings("Duplicates")
public class CommonEntitiesMatrixCreator {
    private final Logger LOGGER = Logger.getLogger("MarkovClusterer");

    public static void main(String[] args) {
        CommonEntitiesMatrixCreator cemc = new CommonEntitiesMatrixCreator();

        try {
            cemc.start();
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

        Handler fileHandler = new FileHandler("./neg_matrix.log");
        fileHandler.setLevel(Level.FINEST);
        fileHandler.setFormatter(new VerySimpleFormatter());
        LOGGER.addHandler(fileHandler);

        // Main variables
        String inputFolder = "texts/input-10";

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

        // Get number of texts and create matrix for storing results in
        int numOfTexts = texts.size();
        Matrix mtrx = new Matrix(numOfTexts, numOfTexts);

        // For all text combinations add common entities % to the matrix
        for (int i = 0; i < numOfTexts; i++) {
            for (int j = 0; j < i + 1; j++) {
                if (i == j) {
                    // If i equals j it is the same text with itself, so they have all the same entities
                    mtrx.set(i, j, 1.0);
                } else {
                    // Check how many common entities the texts have, and how many they have in total
                    TextEntities text1 = texts.get(i);
                    TextEntities text2 = texts.get(j);

                    int totalEntities = text1.getEntities().size() + text2.getEntities().size();
                    System.out.println(i + "x" + j + " -> " + totalEntities);

                    //todo: find common entities and divide them :D
                }
            }
        }
    }
}
