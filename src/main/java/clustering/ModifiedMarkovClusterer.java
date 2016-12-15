package clustering;

import Jama.Matrix;
import entity_extractor.TextEntities;

import java.util.List;

/**
 *
 * @author ggianna
 */
public class ModifiedMarkovClusterer extends MarkovClusterer {

    public ModifiedMarkovClusterer(List<TextEntities> lsSequences) {
        super(lsSequences);
    }

    /**
     * Normalizes a matrix on a per column basis.
     *
     * @param mToNormalize The matrix to normalize <b>in place</b>.
     * @param dPower The power to raise the elements to, before normalization
     * @return The normalized matrix.
     */
    protected Matrix normalizeMatrixPerColumn(Matrix mToNormalize, double dPower) {
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

        return mToNormalize;
    }

}