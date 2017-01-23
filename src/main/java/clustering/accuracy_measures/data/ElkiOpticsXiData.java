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

        // Get algorithm clusters from OPTICSXi ELKI output folder
        File[] files = new File(elkiClustersPath).listFiles();
        if (files != null) {
            for (File file : files) {

                // Ignore settings.txt and optics-clusterorder.txt files
                if (file.isFile() && !file.getName().equals("settings.txt") && !file.getName().equals("optics-clusterorder.txt")) {
                    // Get cluster from the file and add it to the algorithm clusters
                    algClusters.addCluster(getClusterFromFile(file));
                }
            }
        }

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

    /**
     * Get a file that is an OPTICSXi cluster from ELKI, and create a SimpleCluster from it.
     * Sets the name (if found in the file, which it should) and adds the cluster IDs.
     * @param f File to read
     * @return  Cluster made from ELKI output file
     */
    private SimpleCluster getClusterFromFile(File f) {
        SimpleCluster c = new SimpleCluster();

        try {
            // Open the file
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(f), "UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                // Check the line's prefix to process it appropriately
                if (line.startsWith("# Cluster name: ")) {
                    // Get cluster name
                    String name = line.split(" ")[3];

                    c.setName(name);
                } else if (line.startsWith("ID=")) {
                    String idStr = line.split(" ")[0].split("=")[1];

                    c.addText(Integer.parseInt(idStr));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return c;
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
