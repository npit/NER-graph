package clustering.accuracy_measures;

import utils.Binomial;

import java.util.ArrayList;

/**
 * Created by Leo on 2017-01-17.
 */
public class SimpleCluster {
    private ArrayList<Integer> texts;

    public SimpleCluster() {
        texts = new ArrayList<>();
    }

    public void addText(Integer textIndex) {
        texts.add(textIndex);
    }

    public void addAllTexts(ArrayList<Integer> textIndexes) {
        texts.addAll(textIndexes);
    }

    public ArrayList<Integer> getTexts() {
        return texts;
    }

    @Override
    public String toString() {
        return texts.toString();
    }

    /**
     * Return a list with the intersection of the two input lists. Assumes that each item will exist at most once
     * in each list.
     * @param listA
     * @param listB
     * @return List A ∩ List B
     */
    private ArrayList<Integer> intersection(ArrayList<Integer> listA, ArrayList<Integer> listB) {
        ArrayList<Integer> intersection = new ArrayList<>();

        for (Integer i : listA) {
            if (listB.contains(i)) {
                intersection.add(i);
            }
        }

        return intersection;
    }

    /**
     * Calculate the accuracy with which the input cluster "c" reproduces this cluster.
     * @param c Cluster to compare to
     * @return
     */
    public double precision(SimpleCluster c) {
        int commonItems = intersection(this.getTexts(), c.getTexts()).size();
        int cItems = c.getTexts().size();

        return ((double)commonItems) / cItems;
    }

    /**
     * Calculate the completeness with which the input cluster "c" reproduces this cluster.
     * @param c Cluster to compare to
     * @return
     */
    public double recall(SimpleCluster c) {
        int commonItems = intersection(this.getTexts(), c.getTexts()).size();
        int thisItems = this.getTexts().size();

        return ((double)commonItems) / thisItems;
    }

    /**
     * Calculate the precision with which this cluster (assuming it is created by an algorithm) reproduces the given
     * ground truth cluster ("g")
     * @param g             Ground truth cluster that is mapped to this one
     * @param clustersNum   Number of clusters that the algorithm output(ted)
     * @return
     */
    public double clusteringPrecision(SimpleCluster g, int clustersNum) {
        // Calculate number to divide by
        long divider = Binomial.binomial(clustersNum, 2);

        // Find number of items that are also in the ground truth cluster
        int commonItems = intersection(this.getTexts(), g.getTexts()).size();

        // Divide them
        return ((double)commonItems) / divider;
    }
}
