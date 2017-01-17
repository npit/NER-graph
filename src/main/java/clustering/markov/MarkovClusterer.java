/*
 * Copyright 2016 SciFY NPO <info@scify.org>.
 *
 * This product is part of the NewSum Free Software.
 * For more information about NewSum visit
 * 
 *     http://www.scify.gr/site/en/projects/completed/newsum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package clustering.markov;

import Jama.Matrix;
import entity_extractor.TextEntities;
import gr.demokritos.iit.jinsect.documentModel.comparators.NGramCachedGraphComparator;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentWordGraph;
import gr.demokritos.iit.jinsect.structs.GraphSimilarity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ggianna
 */
//extends BaseSimCalculator
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class MarkovClusterer {
    //    Map<Cluster, List<Sequence>> hsSequencesPerCluster = new HashMap<>();
    Map<TextEntities, Cluster> hsClusterPerSequence = new HashMap<>();
    List<TextEntities> origSequences;

    public MarkovClusterer(List<TextEntities> lsSequences) {
        origSequences = new ArrayList<>(lsSequences);
    }

    /**
     * Returns a graph from a TextEntities object. Is used a few times in the algorithm, so to change the method
     * used for clustering change it in this method
     * @param te    Text to get graph for
     * @return      Graph from text
     */
    public static DocumentWordGraph getGraphFromTextEntities(TextEntities te) {
        DocumentWordGraph g = new DocumentWordGraph();
        g.setDataString(te.getEntityTextWithPlaceholderSameSize("A"));

        return g;
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
            mLastRes = iterateMCL(mLastRes, mSims, 5, 10e-5, 3, 3.0);
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

    protected void getClusterAssignment(Matrix mLastRes, List<Cluster> lRes) {
        // Final step: Interprete results
        // For each row (cluster)
        for (int iRow = 0; iRow < mLastRes.getRowDimension(); iRow++) {
            HashSet<TextEntities> hsCur = new HashSet<>(); // Impose uniqueness

            // For all columns (seqs)
            for (int iCol = 0; iCol < mLastRes.getColumnDimension(); iCol++) {
                // If it contains a non-zero element
                if (mLastRes.get(iRow, iCol) > 10e-5) // Add it to the current cluster
                {
                    hsCur.add(origSequences.get(iCol));
                }
            }

            Cluster tCur = new Cluster();
            tCur.addAll(hsCur);

            // If not empty topic
            if (!tCur.isEmpty()) // Add cluster to result set
            {
                lRes.add(tCur);
            }
        }
    }

    protected Matrix iterateMCL(Matrix mLastRes, Matrix mSims, int iNumOfIterations,
                                double dMinDiff, int iMatrixExpansionPower, double dInflationFactor) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        if (iMatrixExpansionPower < 2)
            throw new Exception("Cannot raise to a lower power than 2.");

        // Until convergence or 100 iterations
        for (int iIter = 0; iIter < iNumOfIterations; iIter++) {
            // Expand by squaring the matrix
            mLastRes = mSims.times(mSims);
            // If higher power is required, to it here
            while (--iMatrixExpansionPower > 2)
                mLastRes = mLastRes.times(mSims);

            // Inflate
            normalizeMatrixPerColumn(mLastRes, dInflationFactor);
            // If convergence has been achieved
            if (mSims.minus(mLastRes).normInf() < dMinDiff) {
                Logger.getLogger(this.getClass().getCanonicalName()).log(
                        Level.INFO, "Convergence after {0} iterations...", iIter);
                break;
            }

            // Update sim matrix by copying last result
            mSims = mLastRes.copy();

            // DEBUG LINES
//            try {
//                br.readLine();
//            } catch (IOException ex) {
//                Logger.getLogger(MarkovClusterer.class.getName()).log(Level.FINEST, 
//                        null, ex);
//            }
            Thread.sleep(100); // Wait a while
            //mv.hide();
        }

        Thread.sleep(1000); // Wait a second, to keep last img
        return mLastRes;
    }

    /**
     * Calculates a similarity matrix (including self-similarity), by using NVS
     * calculation.
     *
     * @param lAllSequences
     * @return
     */
    protected Matrix getSimilarityMatrix(List<TextEntities> lAllSequences) {
        // Init sim matrix
        final Matrix mSims = new Matrix(lAllSequences.size(), lAllSequences.size());
        // Perform parallel execution
        ExecutorService es = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
        // Init final vars
        final List<TextEntities> lAllSequencesArg = lAllSequences;
        int iFirstCnt = 0;

        // For every sentence pair in cluster
        final Random r = new Random();
        for (final TextEntities aFirst : lAllSequences) {
            final int iFirstCntArg = iFirstCnt;
            es.submit(new Runnable() {

                @Override
                public void run() {
                    double dSim;
                    int iSecondCnt = 0;

                    NGramCachedGraphComparator ngc = new NGramCachedGraphComparator();
                    // Create first graph
                    DocumentWordGraph gFirst = MarkovClusterer.getGraphFromTextEntities(aFirst);
//                            CSVToFeatures.getGraphFromSequence(aFirst);

                    for (TextEntities aSecond : lAllSequencesArg) {
                        if (iSecondCnt == iFirstCntArg) {
                            dSim = 0.0; // IMPORTANT: Self-similarity is zero
//                            dSim = 1.0; // IMPORTANT: Self-similarity is one
                        } else {
                            dSim = graphToSequenceSimilarity(gFirst, aSecond);
                        }
                        // Set to matrix
                        synchronized (mSims) {
                            mSims.set(iFirstCntArg, iSecondCnt, dSim);
                        }
                        iSecondCnt++;
                    }

                    // DEBUG LINES
                    if (r.nextDouble() < 0.10) {
                        Logger.getLogger(getClass().getCanonicalName()).log(
                                Level.INFO, "Ongoing for seq.{0}", iFirstCntArg);
                    }
                }
            });
            iFirstCnt++;
        }
        // Complete comparisons
        es.shutdown();
        try {
            es.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, null, ex);
            return null;
        }

        // TODO: Use cache?

        return mSims;
    }

    private double graphToSequenceSimilarity(DocumentWordGraph gFirst, TextEntities aSecond) {
        // Create graph for 2nd text
        DocumentWordGraph gSecond = MarkovClusterer.getGraphFromTextEntities(aSecond);

        // Create comparator and compare the 2 graphs
        NGramCachedGraphComparator comparator = new NGramCachedGraphComparator();
        GraphSimilarity gSim = comparator.getSimilarityBetween(gFirst, gSecond);

        // Return NVS
        return (gSim.SizeSimilarity == 0) ? 0.0 : (gSim.ValueSimilarity / gSim.SizeSimilarity);
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

        return mToNormalize;
    }

    protected void collapseClusters(List<Cluster> lInput) {
        List<Cluster> lRes = new ArrayList<>();

        boolean bCollapseOccurred = true;
        while (bCollapseOccurred) {
            bCollapseOccurred = false; // Init collapse flag
            // For every cluster
            ListIterator<Cluster> iFirst = lInput.listIterator();
            List<Cluster> toRemove = new ArrayList<>();
            while (iFirst.hasNext()) {
                Cluster cFirst = iFirst.next();

                // If last cluster
                if (!iFirst.hasNext())
                    break; // We are done
                // For every other cluster
                Iterator<Cluster> iSecond = lInput.listIterator(iFirst.nextIndex());
                while (iSecond.hasNext()) {
                    Cluster cSecond = iSecond.next();

                    // For every item in first
                    boolean bFoundMatch = false;
                    for (TextEntities s : cFirst) {
                        if (cSecond.contains(s)) {
                            bFoundMatch = true;
                            break;
                        }
                    }
                    if (bFoundMatch) {
                        Set<TextEntities> sAll = new HashSet<>();
                        // Collapse both to the first
                        keepUniqueSeqs(sAll, cFirst, cSecond);
                        // Mark second to be removed
                        toRemove.add(cSecond);
                        // Mark collapse
                        bCollapseOccurred = true;
                    }
                }
            }
            if (bCollapseOccurred)
                // Remove collapsed
                lInput.removeAll(toRemove);
        }
    }

    protected void keepUniqueSeqs(Set<TextEntities> sAll, Cluster cFirst, Cluster cSecond) {
        sAll.addAll(cFirst);
        sAll.addAll(cSecond);
        cFirst.clear();
        cFirst.addAll(sAll);
    }

    public void setSequences(List<TextEntities> lsSequences) {
        origSequences = new ArrayList<>(lsSequences);
    }
}