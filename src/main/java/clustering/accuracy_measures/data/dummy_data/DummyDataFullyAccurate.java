package clustering.accuracy_measures.data.dummy_data;

/**
 * Like dummy data 2, but the algorithm's output is made to be exactly the same as the ground truth (to test measures)
 */
public class DummyDataFullyAccurate extends DummyData2 {
    public DummyDataFullyAccurate() {
        super();

        // Make algorithm's clusters the same as ground truth ones.
        this.clusters = this.groundTruth;
    }
}
