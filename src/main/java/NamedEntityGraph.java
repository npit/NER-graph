import entity_extractor.OpenCalaisExtractor;
import entity_extractor.EntityExtractor;
import entity_extractor.TextEntities;

import java.io.File;
import java.util.ArrayList;

public class NamedEntityGraph {
    public static void main(String[] args) {
        // Main variables
        String inputFolder = "texts/input";
        String outputFolder = "texts/output";

        ArrayList<String> apiKeys = new ArrayList<String>();
        apiKeys.add("***REMOVED***");

        File input = new File(inputFolder);
        File output = new File(outputFolder);

        EntityExtractor entityExtractor = new OpenCalaisExtractor();
        entityExtractor.setApiKey(apiKeys.get(0));
        entityExtractor.setOutputDir(output);

        try {
            if (input.isDirectory()) {
                System.out.println("working on all files in " + input.getAbsolutePath());
                for (File file : input.listFiles()) {
                    if (file.isFile()) {
                        System.out.println("[main] Getting entities for " + file);
                        TextEntities entities = entityExtractor.getEntities(file);
                        System.out.println("[main] Got extracted entities for " + file);
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
