package clustering.accuracy_measures.data;

import clustering.accuracy_measures.SimpleCluster;

/**
 * Like DummyData, but with more clusters and texts!
 */
public class DummyData2 extends DummyData {
    public DummyData2() {
        super();

        SimpleCluster temp;

        // Compared to DummyData, our fake data will have 5 ground truth clusters, and 4 non-existent algorithm's
        // clusters, and also the total number of texts will be 20
        this.textsNum = 20;

        // Add 2 ground truth clusters
        temp = new SimpleCluster();
        temp.addText(16);
        temp.addText(18);
        temp.addText(19);
        temp.addText(11);
        temp.addText(15);
        temp.addText(12);
        this.groundTruth.addCluster(temp);

        temp = new SimpleCluster();
        temp.addText(14);
        temp.addText(13);
        temp.addText(10);
        temp.addText(17);
        this.groundTruth.addCluster(temp);

        // Add 2 algorithm clusters
        temp = new SimpleCluster();
        temp.addText(16);
        temp.addText(18);
        temp.addText(11);
        temp.addText(19);
        temp.addText(17);
        temp.addText(15);
        temp.addText(12);
        this.clusters.addCluster(temp);

        temp = new SimpleCluster();
        temp.addText(14);
        temp.addText(10);
        temp.addText(13);
        this.clusters.addCluster(temp);
    }
}
