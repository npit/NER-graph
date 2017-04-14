package clustering.accuracy_measures.data;

import clustering.accuracy_measures.Clusters;
import clustering.accuracy_measures.SimpleCluster;
import utils.GroundTruthReader;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Reads the output folder of ELKI for the OPTICSXi algorithm to provide the clustering results to the rest of the
 * program for calculating the clustering accuracy measures
 */
public class ElkiOpticsXiData implements ClustersData {
    private Clusters algClusters;
    private Clusters groundTruth;
    private int textsNum;

    public ElkiOpticsXiData(String elkiClustersPath, String groundTruthFilePath) {
        // Initialize variables
        algClusters = new Clusters();
        groundTruth = GroundTruthReader.getClusters(groundTruthFilePath);
        textsNum = -1;

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

        // Generate array of all texts in order to find largest ID
        ArrayList<Integer> allTextIDs = new ArrayList<>();
        for (SimpleCluster sc : algClusters.getClusters()) {
            allTextIDs.addAll(sc.getTexts());
        }

        // Number of texts is the largest ID + 1
        this.textsNum = Collections.max(allTextIDs) + 1;
    }

    /**
     * Get a file that is an OPTICSXi cluster from ELKI, and create a SimpleCluster from it.
     * Sets the name (if found in the file, which it should) and adds the cluster IDs.
     *
     * @param f File to read
     * @return Cluster made from ELKI output file
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
        return algClusters;
    }

    @Override
    public Clusters getGroundTruthClusters() {
        return groundTruth;
    }

    @Override
    public int numOfGroundTruthClusters() {
        return algClusters.getClustersNum();
    }

    @Override
    public int numOfAlgorithmClusters() {
        return groundTruth.getClustersNum();
    }

    @Override
    public int numOfTexts() {
        return textsNum;
    }
}
