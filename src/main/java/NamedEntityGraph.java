import entity_extractor.EntityExtractor;
import entity_extractor.TextEntities;
import entity_extractor.open_calais_extractor.OpenCalaisExtractor;
import gr.demokritos.iit.jinsect.documentModel.comparators.NGramCachedGraphComparator;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentWordGraph;
import gr.demokritos.iit.jinsect.structs.GraphSimilarity;

import java.io.File;
import java.util.ArrayList;

public class NamedEntityGraph {
    public static void main(String[] args) {
        // Main variables
        String inputFolder = "texts/input";

        File input = new File(inputFolder);
        EntityExtractor entityExtractor = new OpenCalaisExtractor();
        ArrayList<DocumentWordGraph> entityGraphs = new ArrayList<>();
        ArrayList<DocumentNGramGraph> nGramGraphs = new ArrayList<>();

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

                        // Create entity word graph
                        DocumentWordGraph wordGraph = new DocumentWordGraph();
                        wordGraph.setDataString(entityText);

                        entityGraphs.add(wordGraph);

                        // Create normal n-gram graph
                        DocumentNGramGraph nGramGraph = new DocumentWordGraph();
                        nGramGraph.setDataString(entities.getText());

                        nGramGraphs.add(nGramGraph);

                        System.out.println("[main] Got " + entities.getEntities().size() + " extracted entities from " + file + "\n");
                    } else {
                        System.out.println("Skipping " + file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create comparator object
        NGramCachedGraphComparator comparator = new NGramCachedGraphComparator();

        // Compare 2 first entity graphs
        if (entityGraphs.size() >= 2) {
            // Get graphs to compare
            DocumentWordGraph entityGraph1 = entityGraphs.get(0);
            DocumentWordGraph entityGraph2 = entityGraphs.get(1);

            // Get similarity of graphs
            GraphSimilarity entitySimilarity = comparator.getSimilarityBetween(entityGraph1, entityGraph2);

            double entitySimVal = entitySimilarity.getOverallSimilarity();
            System.out.println("Named Entity Graph similarity:\t" + entitySimVal);
        }

        // Compare 2 first n-gram graphs
        if (nGramGraphs.size() >= 2) {
            DocumentNGramGraph nGramGraph1 = nGramGraphs.get(0);
            DocumentNGramGraph nGramGraph2 = nGramGraphs.get(1);

            // Get similarity of n-gram graphs
            GraphSimilarity nGramSimilarity = comparator.getSimilarityBetween(nGramGraph1, nGramGraph2);

            double nGramSimVal = nGramSimilarity.getOverallSimilarity();
            System.out.println("N-gram Graph similarity:\t\t" + nGramSimVal);
        }
    }
}
