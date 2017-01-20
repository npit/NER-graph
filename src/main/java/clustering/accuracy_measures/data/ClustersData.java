package clustering.accuracy_measures.data;

import clustering.accuracy_measures.Clusters;

/**
 * Interface for class to get clustering data (from ELKI, random data, or elsewhere)...
 */
public interface ClustersData {
    Clusters getAlgorithmClusters();

    Clusters getGroundTruthClusters();

    int numOfGroundTruthClusters();

    int numOfAlgorithmClusters();

    int numOfTexts();
}
