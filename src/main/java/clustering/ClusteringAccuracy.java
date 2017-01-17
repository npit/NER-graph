package clustering;

import clustering.accuracy_measures.Clusters;
import clustering.accuracy_measures.DummyData;

/**
 * Uses a class for getting ground truth clusters and clusters created by an algorithm, and measures their accuracy
 */
public class ClusteringAccuracy {
    public static void main(String[] args) {
        ClusteringAccuracy ca = new ClusteringAccuracy();

        ca.start();
    }

    private void start() {
        //todo
        DummyData data = new DummyData();
        Clusters g = data.getGroundTruthClusters();
        Clusters a = data.getAlgorithmClusters();
    }
}
