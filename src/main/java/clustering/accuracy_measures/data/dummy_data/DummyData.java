package clustering.accuracy_measures.data.dummy_data;

import clustering.accuracy_measures.Clusters;
import clustering.accuracy_measures.SimpleCluster;
import clustering.accuracy_measures.data.ClustersData;

/**
 * Create dummy cluster data to test accuracy measures with before implementing the actual class that will read
 * the clusters from ELKI's output files
 */
public class DummyData implements ClustersData {
    Clusters clusters;
    Clusters groundTruth;
    int textsNum = 10;

    public DummyData() {
        // Create dummy_data data
        clusters = new Clusters();
        groundTruth = new Clusters();

        SimpleCluster tempCluster;

        // Assuming we will have 10 texts, the ground truth is that they should be 3 clusters which contain text indexes:
        // cluster #0: 2, 4, 5
        // cluster #1: 1, 3, 7, 8
        // cluster #2: 0, 6, 9

        // Ground truth cluster #0
        tempCluster = new SimpleCluster();
        tempCluster.addText(2);
        tempCluster.addText(4);
        tempCluster.addText(5);
        groundTruth.addCluster(tempCluster);

        // Ground truth cluster #1
        tempCluster = new SimpleCluster();
        tempCluster.addText(1);
        tempCluster.addText(3);
        tempCluster.addText(7);
        tempCluster.addText(8);
        groundTruth.addCluster(tempCluster);

        // Ground truth cluster #2
        tempCluster = new SimpleCluster();
        tempCluster.addText(0);
        tempCluster.addText(6);
        tempCluster.addText(9);
        groundTruth.addCluster(tempCluster);

        // Create non-existent algorithm's clusters
        // cluster #0: 2, 4, 6, 9
        // cluster #1: 0, 1, 3, 5, 7, 8

        // Fake algorithm cluster #0
        tempCluster = new SimpleCluster();
        tempCluster.addText(2);
        tempCluster.addText(4);
        tempCluster.addText(6);
        tempCluster.addText(9);
        clusters.addCluster(tempCluster);

        // Fake algorithm cluster #1
        tempCluster = new SimpleCluster();
        tempCluster.addText(0);
        tempCluster.addText(1);
        tempCluster.addText(3);
        tempCluster.addText(5);
        tempCluster.addText(7);
        tempCluster.addText(8);
        clusters.addCluster(tempCluster);
    }

    @Override
    public Clusters getAlgorithmClusters() {
        return clusters;
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
        return clusters.getClustersNum();
    }

    @Override
    public int numOfTexts() {
        return textsNum;
    }
}
