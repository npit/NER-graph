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
        DummyData data = new DummyData();
        Clusters g = data.getGroundTruthClusters();
        Clusters a = data.getAlgorithmClusters();

        System.out.println(g);
        System.out.println(a);

        // todo: Find mapping (f) of ground truth clusters -> algorithm's clusters

        // todo: Calculate Pr (weighted avg. of PRi's over all ground truth clusters)

        // todo: Calculate Re (weighted avg. of Rei's over all ground truth clusters)

        // todo: Calculate CPr (avg of CPRi's)

        // todo: Calculate PCPr
        //noinspection StatementWithEmptyBody
        if (g.getClustersNum() > a.getClustersNum()) {
            // k/k' * CPr
        } else {
            // k'/k * CPr
        }
    }
}
