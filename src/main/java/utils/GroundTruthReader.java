package utils;

import clustering.accuracy_measures.Clusters;
import clustering.accuracy_measures.SimpleCluster;

import java.io.*;

/**
 * Reads the ground truth clusters from a file with the required format (see python script output to see the format)
 */
public class GroundTruthReader {
    public static Clusters getClusters(String path) {
        Clusters groundTruth = new Clusters();

        try {
            // Open the file
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(new File(path)), "UTF-8"));

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

                // Add each ID to the new cluster
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

        return groundTruth;
    }
}
