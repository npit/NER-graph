package clustering.accuracy_measures;

/**
 * Interface for class to get clustering data (from ELKI, random data, or elsewhere)...
 */
public interface ClustersData {
    Clusters getAlgorithmClusters();

    Clusters getGroundTruthClusters();
}
