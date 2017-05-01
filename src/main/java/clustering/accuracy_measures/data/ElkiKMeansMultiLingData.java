package clustering.accuracy_measures.data;

import clustering.accuracy_measures.Clusters;
import clustering.accuracy_measures.SimpleCluster;
import utils.GroundTruthReader;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ElkiKMeansMultiLingData implements ClustersData {
    private int textsNum;
    private Clusters algorithm;
    private Clusters groundTruth;

    public ElkiKMeansMultiLingData(String kmeansClustersFile, String groundTruthFilePath, String textIdsPath) {
        // Get ground truth
        groundTruth = GroundTruthReader.getClusters(groundTruthFilePath);

        // Get IDs of texts
        Map<String, Integer> textIds = new HashMap<>();
        File idsFile = new File(textIdsPath);
        if (idsFile.isFile()) {
            try {
                // Open the file
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(idsFile), "UTF-8"));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Each line is a mapping of a text to its ID
                    String[] split = line.split(" ");

                    Integer id = Integer.valueOf(split[1]);
                    String name = split[0].split("\\.")[0];   // keep only number part (discard .LANG)

                    textIds.put(name, id);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Get texts number
        textsNum = textIds.keySet().size();

        // Get algorithm's clusters
        algorithm = new Clusters();
        File in = new File(kmeansClustersFile);
        if (in.isFile()) {
            try {
                // Open the file
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(in), "UTF-8"));

                String line;
                int lineNum = 0;
                while ((line = reader.readLine()) != null) {
                    // Each line is a cluster
                    SimpleCluster c = new SimpleCluster("line_" + lineNum++);

                    String[] clusterTexts = line.split(" ");

                    for (String s : clusterTexts) {
                        c.addText(textIds.get(s));
                    }

                    algorithm.addCluster(c);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Clusters getAlgorithmClusters() {
        return algorithm;
    }

    @Override
    public Clusters getGroundTruthClusters() {
        return groundTruth;
    }

    @Override
    public int numOfGroundTruthClusters() {
        return groundTruth.getClustersNum();
    }

    @Override
    public int numOfAlgorithmClusters() {
        return algorithm.getClustersNum();
    }

    @Override
    public int numOfTexts() {
        return textsNum;
    }
}
