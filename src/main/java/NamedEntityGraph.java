import entity_extractor.EntityExtractor;
import entity_extractor.open_calais_extractor.OpenCalaisExtractor;
import entity_extractor.TextEntities;

import java.io.File;

public class NamedEntityGraph {
    public static void main(String[] args) {
        // Main variables
        String inputFolder = "texts/input";

        File input = new File(inputFolder);
        EntityExtractor entityExtractor = new OpenCalaisExtractor();

        try {
            if (input.isDirectory()) {
                System.out.println("working on all files in " + input.getAbsolutePath());
                for (File file : input.listFiles()) {
                    if (file.isFile()) {
                        System.out.println("[main] Getting entities for " + file);

                        TextEntities entities = entityExtractor.getEntities(file);
                        entities.printEntities();

                        System.out.println("[main] Got " + entities.getEntities().size() + " extracted entities from " + file);
                    } else {
                        System.out.println("skipping "+file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
