import entity_extractor.EntityExtractor;
import entity_extractor.TextEntities;
import entity_extractor.open_calais_extractor.OpenCalaisExtractor;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentWordGraph;

import java.io.File;
import java.util.ArrayList;

public class NamedEntityGraph {
    public static void main(String[] args) {
        // Main variables
        String inputFolder = "texts/input";

        File input = new File(inputFolder);
        EntityExtractor entityExtractor = new OpenCalaisExtractor();
        ArrayList<DocumentWordGraph> graphs = new ArrayList<>();

        try {
            if (input.isDirectory()) {
                System.out.println("working on all files in " + input.getAbsolutePath());
                for (File file : input.listFiles()) {
                    if (file.isFile()) {
                        System.out.println("[main] Getting entities for " + file);

                        TextEntities entities = entityExtractor.getEntities(file);
//                        entities.printEntities();

                        String entityText = entities.getEntityText();
                        System.out.println(entityText);

                        // Create a word graph
                        DocumentWordGraph wordGraph = new DocumentWordGraph();
                        wordGraph.setDataString(entityText);

                        graphs.add(wordGraph);

                        System.out.println("[main] Got " + entities.getEntities().size() + " extracted entities from " + file + "\n");
                    } else {
                        System.out.println("Skipping " + file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
