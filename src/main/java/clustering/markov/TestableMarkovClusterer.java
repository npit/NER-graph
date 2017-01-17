package clustering.markov;

import Jama.Matrix;
import entity_extractor.TextEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by leots on 2016-12-15.
 */
public class TestableMarkovClusterer extends MarkovClusterer {

    private boolean useModifiedNormalization;
    private int iNumOfIterations;
    private double inflationFactor;

    public TestableMarkovClusterer(List<TextEntities> lsSequences, boolean useModifiedNormalization, int iNumOfIterations, double inflationFactor) {
        super(lsSequences);

        this.useModifiedNormalization = useModifiedNormalization;
        this.iNumOfIterations = iNumOfIterations;
        this.inflationFactor = inflationFactor;
    }

    public List<Cluster> calculateClusters() {
        // Get clusters of sequences
        List<Cluster> lClusters = getSequenceClusters();
        // Update reverse index (article -> cluster)
        // For every cluster
        for (Cluster cCur : lClusters) {
            // For every sequence in cluster
            for (TextEntities aCur : cCur) {
                // Update map of sequence -> cluster (name)
                hsClusterPerSequence.put(aCur, cCur);
            }
        }

        return lClusters;
    }

    protected List<Cluster> getSequenceClusters() {
        // Init return map
        List<Cluster> lRes = new ArrayList<>();

        // Get similarities
        Matrix mSims = getSimilarityMatrix(origSequences);
        // Initial step
        // Normalize per column to render stochastic
        normalizeMatrixPerColumn(mSims, 1.0);
        Matrix mLastRes = null;

        try {
            //original:
//            mLastRes = iterateMCL(mLastRes, mSims, 5, 10e-5, 3, 3.0);

            //modified:
            mLastRes = iterateMCL(mLastRes, mSims, iNumOfIterations, 10e-5, 3, inflationFactor);
        } catch (Exception ex) {
            Logger.getLogger(MarkovClusterer.class.getName()).log(Level.SEVERE,
                    "Could not iterate.", ex);
            return null;
        }

        getClusterAssignment(mLastRes, lRes);

        collapseClusters(lRes);

        // Return list
        return lRes;
    }

    /**
     * Normalizes a matrix on a per column basis.
     *
     * @param mToNormalize The matrix to normalize <b>in place</b>.
     * @param dPower The power to raise the elements to, before normalization
     * @return The normalized matrix.
     */
    @SuppressWarnings("Duplicates")
    protected Matrix normalizeMatrixPerColumn(Matrix mToNormalize, double dPower) {
        if (useModifiedNormalization) {
            // modified normalization function
            // For every column
            for (int iColumnCnt = 0; iColumnCnt < mToNormalize.getColumnDimension(); iColumnCnt++) {
                // Determine sum
                double dColSum = 0.0;
                // For every row
                for (int iRowCnt = 0; iRowCnt < mToNormalize.getRowDimension(); iRowCnt++) {
                    double dPowered = Math.pow(mToNormalize.get(iRowCnt, iColumnCnt), dPower);
                    // Update matrix value
                    mToNormalize.set(iRowCnt, iColumnCnt, dPowered);
                    // Update maximum
                    dColSum = (dPowered > dColSum) ? dPowered : dColSum;
                }

                // For every row
                for (int iRowCnt = 0; iRowCnt < mToNormalize.getRowDimension(); iRowCnt++) {
                    double dNormalized = mToNormalize.get(iRowCnt, iColumnCnt) / dColSum;
                    // Update matrix value to normalized value
                    mToNormalize.set(iRowCnt, iColumnCnt, dNormalized);
                }
            }
        } else {
            // normal normalization function
            // For every column
            for (int iColumnCnt = 0; iColumnCnt < mToNormalize.getColumnDimension(); iColumnCnt++) {
                // Determine sum
                double dColSum = 0.0;
                // For every row
                for (int iRowCnt = 0; iRowCnt < mToNormalize.getRowDimension(); iRowCnt++) {
                    double dPowered = Math.pow(mToNormalize.get(iRowCnt, iColumnCnt), dPower);
                    // Update matrix value, setting zero elements quickly
                    if (dPowered < 10e-10)
                        dPowered = 0.0;
                    mToNormalize.set(iRowCnt, iColumnCnt, dPowered);
                    // Update sum
                    dColSum += dPowered;
                }

                // For every row
                for (int iRowCnt = 0; iRowCnt < mToNormalize.getRowDimension(); iRowCnt++) {
                    double dNormalized = mToNormalize.get(iRowCnt, iColumnCnt) / dColSum;
                    // Update matrix value to normalized value
                    mToNormalize.set(iRowCnt, iColumnCnt, dNormalized);
                }
            }
        }

        return mToNormalize;
    }

}
