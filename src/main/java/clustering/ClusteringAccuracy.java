package clustering;

import clustering.accuracy_measures.Clusters;
import clustering.accuracy_measures.DummyData;
import clustering.accuracy_measures.SimpleCluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        Clusters gClusters = data.getGroundTruthClusters();
        Clusters aClusters = data.getAlgorithmClusters();

        // Find mapping (f) of ground truth clusters -> algorithm's clusters
        // (for each cluster in 'G' find the one in the 'C' which has the highest percentage of common elements)
        Map<SimpleCluster, SimpleCluster> f = new HashMap<>();

        for (SimpleCluster g : gClusters.getClusters()) {
            double highestPercent = -1.0;

            for (SimpleCluster c : aClusters.getClusters()) {
                // Find percentage of common elements
                double percent = percentOfCommonItems(g, c);

                // If it's bigger than the current highest percent, save it to the HashMap f
                if (percent > highestPercent) {
                    highestPercent = percent;

                    f.put(g, c); // f(g) = c
                }
            }

            System.out.println("f(" + g + ") = " + f.get(g) + " --> " + highestPercent + " similarity");
        }

        // todo: Calculate Pr (weighted avg. of PRi's over all ground truth clusters)

        // todo: Calculate Re (weighted avg. of Rei's over all ground truth clusters)

        // todo: Calculate CPr (avg of CPRi's)

        // todo: Calculate PCPr
        //noinspection StatementWithEmptyBody
        if (gClusters.getClustersNum() > aClusters.getClustersNum()) {
            // k/k' * CPr
        } else {
            // k'/k * CPr
        }
    }

    /**
     * Find the percentage of common elements between 2 clusters
     * @param a Cluster 1
     * @param b Cluster 2
     * @return  Double, in range [0, 1]
     */
    private double percentOfCommonItems(SimpleCluster a, SimpleCluster b) {
        ArrayList<Integer> textsA = a.getTexts();
        ArrayList<Integer> textsB = b.getTexts();

        int commonElements = 0;
        int totalElements = textsA.size() + textsB.size();

        // Count common elements (each text can only exist 1 time in a cluster btw)
        for (Integer i : textsA) {
            for (Integer j : textsB) {
                if (i.equals(j)) {
                    // Add the 2 numbers to the common elements count
                    commonElements += 2;

                    // Continue on to next number
                    break;
                }
            }
        }

        // Return common / total elements
        return ((double)commonElements) / totalElements;
    }
}
