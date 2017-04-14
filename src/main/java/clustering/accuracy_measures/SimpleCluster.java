package clustering.accuracy_measures;

import utils.Binomial;

import java.util.ArrayList;

public class SimpleCluster {
    private ArrayList<Integer> texts;
    private String name;

    public SimpleCluster() {
        texts = new ArrayList<>();
    }

    public SimpleCluster(String name) {
        texts = new ArrayList<>();
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        if (name != null) {
            return name + ": " + texts.toString();
        } else {
            return texts.toString();
        }
    }

    /**
     * Return a list with the intersection of the two input lists. Assumes that each item will exist at most once
     * in each list.
     *
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
     *
     * @param c Cluster to compare to
     * @return
     */
    public double precision(SimpleCluster c) {
        int commonItems = intersection(this.getTexts(), c.getTexts()).size();
        int cItems = c.getTexts().size();

        return ((double) commonItems) / cItems;
    }

    /**
     * Calculate the completeness with which the input cluster "c" reproduces this cluster.
     *
     * @param c Cluster to compare to
     * @return
     */
    public double recall(SimpleCluster c) {
        int commonItems = intersection(this.getTexts(), c.getTexts()).size();
        int thisItems = this.getTexts().size();

        return ((double) commonItems) / thisItems;
    }

    /**
     * Calculate the precision with which this cluster (assuming it is created by an algorithm) reproduces the given
     * ground truth cluster ("g")
     *
     * @param g           Ground truth cluster that is mapped to this one
     * @param clustersNum Number of clusters that the algorithm output(ted)
     * @return
     */
    public double clusteringPrecision(SimpleCluster g, int clustersNum) {
        // Calculate number to divide by
        long divider = Binomial.binomial(clustersNum, 2);

        // Find number of items that are also in the ground truth cluster
        int commonPairs = 0;

        // For each pair of items in this cluster, check if it exists in "g" too
        for (int i = 0; i < texts.size(); i++) {
            for (int j = i + 1; j < texts.size(); j++) {
                Integer t = texts.get(i);
                Integer s = texts.get(j);

//                System.out.print("t,s: " + t + " - " + s);

                if (g.getTexts().contains(t) && g.getTexts().contains(s)) {
                    commonPairs++;
//                    System.out.println(" (exists in g too)");
//                } else {
//                    System.out.println(" (not in g...)");
                }

            }
        }

//        System.out.println("common pairs: " + commonPairs);
//        System.out.println("divider: " + divider);
//        System.out.println("cpri: " + ((double) commonPairs) / divider);

        // Divide them
        return ((double) commonPairs) / divider;
    }
}
