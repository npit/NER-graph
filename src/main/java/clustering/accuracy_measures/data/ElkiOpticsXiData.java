package clustering.accuracy_measures.data;

import clustering.accuracy_measures.Clusters;
import clustering.accuracy_measures.SimpleCluster;

import java.io.*;

/**
 * Reads the output folder of ELKI for the OPTICSXi algorithm to provide the clustering results to the rest of the
 * program for calculating the clustering accuracy measures
 */
public class ElkiOpticsXiData implements ClustersData {
    Clusters algClusters;
    Clusters groundTruth;

    public ElkiOpticsXiData(String elkiClustersPath, String groundTruthFilePath) {
        // Initialize arraylists
        algClusters = new Clusters();
        groundTruth = new Clusters();

        //todo: Get algorithm clusters from OPTICSXi ELKI output folder

        // Get ground truth clusters from python-exported ground truth text file
        try {
            // Open the file
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(new File(groundTruthFilePath)), "UTF-8"));

            // For each line (which represents a ground truth cluster) read its name and the text IDs that are in it
            String line;
            while ((line = reader.readLine()) != null) {
                // Split line into cluster "name" and ids that are in it
                String[] parts = line.split("\\|");

                // Get name and IDs
                String coverageStr = parts[0];
                String[] ids = parts[1].split(" ");

                // Create the new cluster
                SimpleCluster c = new SimpleCluster();
                c.setName(coverageStr);

                for (String idStr : ids) {
                    Integer id = Integer.parseInt(idStr);
                    c.addText(id);
                }

                // Add the cluster to the other clusters
                groundTruth.addCluster(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Clusters getAlgorithmClusters() {
        return null;
    }

    @Override
    public Clusters getGroundTruthClusters() {
        return null;
    }

    @Override
    public int numOfGroundTruthClusters() {
        return 0;
    }

    @Override
    public int numOfAlgorithmClusters() {
        return 0;
    }

    @Override
    public int numOfTexts() {
        return 0;
    }
}
