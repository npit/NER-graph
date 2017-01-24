import Jama.Matrix;
import entity_extractor.EntityExtractor;
import entity_extractor.ExtractedEntity;
import entity_extractor.OpenCalaisExtractor;
import entity_extractor.TextEntities;
import utils.Percentage;
import utils.VerySimpleFormatter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        String inputFolder = "texts/input-25";
        String csvOutput = "common-entities-matrix-25.csv";

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

        int zeroCommons = 0;

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

                    // Get total number of unique entities
                    ArrayList<ExtractedEntity> allEntities = new ArrayList<>();
                    allEntities.addAll(text1.getEntities());
                    allEntities.addAll(text2.getEntities());
                    int totalEntities = uniqueEntities(allEntities).size();

                    // Assuming that same "name" attribute == same entity, find the common ones
                    int commonEntities = 0;

                    // For each (unique) entity of text1, check if there is one in the other text with the same name
                    for (ExtractedEntity text1Entity : uniqueEntities(text1.getEntities())) {
                        for (ExtractedEntity text2Entity : uniqueEntities(text2.getEntities())) {
                            if (text1Entity.getName().equals(text2Entity.getName())) {
                                // Count entity as existing in both texts
                                commonEntities++;

                                // We found that the entity exists in text2, so skip other entities of text 2
                                break;
                            }
                        }
                    }

                    double result = ((double)commonEntities) / totalEntities;
//                    System.out.println(i + "x" + j + " -> " + commonEntities + "/" + totalEntities + " ===> " + result);

                    // Set the result on the 2 positions of the matrix (symmetric matrix)
                    mtrx.set(i, j, result);
                    mtrx.set(j, i, result);

                    // If there were no common entities, count it for printing stats later
                    if (commonEntities == 0) {
                        zeroCommons++;
                    }
                }
            }
        }

        System.out.println("Comparisons which had 0 common entities: " + zeroCommons);

        // Print matrix
        for (int i = 0; i < numOfTexts; i++) {
            for (int j = 0; j < numOfTexts; j++) {
                System.out.print(String.format("%1$,.3f", mtrx.get(i, j)) + "\t");
            }

            System.out.println();
        }

        // Export matrix to CSV file
        try {
            PrintWriter writer = new PrintWriter(csvOutput, "UTF-8");

            for (int i = 0; i < numOfTexts; i++) {
                for (int j = 0; j < numOfTexts; j++) {
                    writer.print(mtrx.get(i, j));

                    // Add comma except for the end of the line
                    if (j < numOfTexts - 1) {
                        writer.print(",");
                    }
                }

                writer.println();
            }

            writer.close();
        } catch(IOException e) {
            System.err.println("Couldn't write file :( " + e.getMessage());
        }
    }

    /**
     * Gets a list of extracted entities, and returns another one which contains each entity only once
     * (compares them by name)
     * @param entities  Entities with duplicates
     * @return          Entities without duplicates
     */
    private List<ExtractedEntity> uniqueEntities(List<ExtractedEntity> entities) {
        List<ExtractedEntity> unique = new ArrayList<>();

        for (ExtractedEntity e : entities) {
            // If the entity is not in the unique list, add it
            if (!unique.contains(e)) {
                unique.add(e);
            }
        }

        return unique;
    }
}
