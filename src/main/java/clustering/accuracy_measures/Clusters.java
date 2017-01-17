package clustering.accuracy_measures;

import java.util.ArrayList;

/**
 * Created by Leo on 2017-01-17.
 */
public class Clusters {
    private ArrayList<SimpleCluster> clusters;

    public Clusters() {
        clusters = new ArrayList<>();
    }

    public int getClustersNum() {
        return clusters.size();
    }

    public void addCluster(SimpleCluster c) {
        clusters.add(c);
    }

    public void addAllClusters(ArrayList<SimpleCluster> clustersToAdd) {
        clusters.addAll(clustersToAdd);
    }

    public ArrayList<SimpleCluster> getClusters() {
        return clusters;
    }

    @Override
    public String toString() {
        return clusters.toString();
    }
}
