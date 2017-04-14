package clustering.accuracy_measures.data;

import clustering.accuracy_measures.Clusters;
import clustering.accuracy_measures.SimpleCluster;
import utils.GroundTruthReader;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class RandomData implements ClustersData {
    private Clusters groundTruth;
    private Clusters randomClusters;
    private int textsNumber;

    public RandomData(int textsNumber, String groundTruthFilePath) {
        // Save texts number and read the ground truth clusters from file
        this.textsNumber = textsNumber;
        groundTruth = GroundTruthReader.getClusters(groundTruthFilePath);

        // Decide on number of clusters (it will be the max number, and will range from having all texts in 1 cluster,
        // to each text being in a separate cluster on its own)
        int clustersNum = ThreadLocalRandom.current().nextInt(1, textsNumber + 1);

        // Create empty clusters
        ArrayList<SimpleCluster> rngClusters = new ArrayList<>();

        for (int i = 0; i < clustersNum; i++) {
            rngClusters.add(new SimpleCluster("Random_" + String.valueOf(i + 1)));
        }

        // Put each text into one of the clusters
        for (int i = 0; i < textsNumber; i++) {
            // Decide which cluster this text will go into
            int clusterNum = ThreadLocalRandom.current().nextInt(0, clustersNum);

            // Add the text to the cluster
            rngClusters.get(clusterNum).addText(i);
        }

        randomClusters = new Clusters();
        //todo: what happens if some clusters are empty?
        randomClusters.addAllClusters(rngClusters);
    }

    @Override
    public Clusters getAlgorithmClusters() {
        return randomClusters;
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
        return randomClusters.getClustersNum();
    }

    @Override
    public int numOfTexts() {
        return this.textsNumber;
    }
}
