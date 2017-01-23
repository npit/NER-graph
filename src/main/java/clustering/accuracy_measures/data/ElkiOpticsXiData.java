package clustering.accuracy_measures.data;

import clustering.accuracy_measures.Clusters;

/**
 * Reads the output folder of ELKI for the OPTICSXi algorithm to provide the clustering results to the rest of the
 * program for calculating the clustering accuracy measures
 */
public class ElkiOpticsXiData implements ClustersData {
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
