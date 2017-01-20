package clustering;

import clustering.accuracy_measures.Clusters;
import clustering.accuracy_measures.data.DummyData;
import clustering.accuracy_measures.data.DummyData2;
import clustering.accuracy_measures.SimpleCluster;
import clustering.accuracy_measures.data.DummyDataFullyAccurate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Uses a class for getting ground truth clusters and clusters created by an algorithm, and measures their accuracy
 */
public class ClusteringAccuracy {
    public static void main(String[] args) {
        ClusteringAccuracy ca = new ClusteringAccuracy();

        ca.start();
    }

    private void start() {
        DummyData data = new DummyDataFullyAccurate();
        Clusters gClusters = data.getGroundTruthClusters();
        Clusters aClusters = data.getAlgorithmClusters();
        int textsNum = data.numOfTexts();

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

            System.out.println("f(" + g + ") = " + f.get(g) + " --> " + String.format("%1$,.2f", highestPercent * 100 ) + " % of common items");
        }

        // Calculate Pr (weighted avg. of PRi's over all ground truth clusters)
        //todo: confirm that R is the set with the clustered texts, so that |R| from the paper is indeed textsNum
        double precision = 0;

        for (SimpleCluster g : gClusters.getClusters()) {
            double weight = ((double)g.getTexts().size()) / textsNum;

            precision += weight * g.precision(f.get(g));
        }

        System.out.println("Total precision (Pr): " + precision);


        // Calculate Re (weighted avg. of Rei's over all ground truth clusters)
        double recall = 0;

        for (SimpleCluster g : gClusters.getClusters()) {
            double weight = ((double)g.getTexts().size()) / textsNum;

            recall += weight * g.recall(f.get(g));
        }

        System.out.println("Total recall (Re): " + recall);

        // Calculate F1 (harmonic mean of precision and recall)
        double f1 = (2 * precision * recall) / (precision + recall);

        System.out.println("F1-measure: " + f1);

        // Calculate CPr (avg of CPRi's)
        double clusteringPrecision = 0;
        int divider = 0;

        for (SimpleCluster g : gClusters.getClusters()) {
            SimpleCluster c = f.get(g);

            // Skip clusters of size 1
            if (c.getTexts().size() > 1) {
                double cpri = c.clusteringPrecision(g, aClusters.getClustersNum());

                clusteringPrecision += cpri;
                divider++;
            }
        }

        if (divider > 0) {
            clusteringPrecision = clusteringPrecision / divider;

            System.out.println("Clustering precision: " + clusteringPrecision);
        } else {
            System.err.println("Error with clustering precision: no clusters with size > 1?");
        }

        // Calculate PCPr (penalised clustering precision)
        double multiplier;
        if (gClusters.getClustersNum() < aClusters.getClustersNum()) {
            // Will do k/k' * CPr
            multiplier = ((double)gClusters.getClustersNum()) / aClusters.getClustersNum();
        } else {
            // Will do k'/k * CPr
            multiplier = ((double)aClusters.getClustersNum()) / gClusters.getClustersNum();
        }

        double pcpr = multiplier * clusteringPrecision;

        System.out.println("Penalised Clustering Precision: " + pcpr);
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
