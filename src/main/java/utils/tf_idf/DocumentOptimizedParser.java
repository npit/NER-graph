package utils.tf_idf;

import entity_extractor.TextEntities;
import gr.demokritos.iit.conceptualIndex.structs.Distribution;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentOptimizedParser extends DocumentParser {
    Map<String, List<Pair<String, Double>>> mTopTermsPerDocument = new HashMap<>();

    @Override
    public void parseFiles(List<TextEntities> documents) {
        // Init overall TF struct
        HashMap<String, Distribution<String>> hTFsPerDoc = new HashMap<>();

        // Init terms histogram overall (IDF)
        Distribution<String> dTermsInDocs = new Distribution<>();
        // For each text
        for (TextEntities t : documents) {
            // Init terms local histogram (TF)
            Distribution<String> dTerms = new Distribution<>();
            // Normalize and split text
            String[] saTerms = t.getText().toUpperCase().replaceAll("[\\W&&[^\\s]]", " ").split("\\W+");
            // For each term
            for (String sTerm : saTerms) {
                // Update the corresponding local entries (TF)
                dTerms.increaseValue(sTerm, 1.0);
            }
            // Update overall TF struct for doc
            hTFsPerDoc.put(t.getTitle(), dTerms);

            // For all terms in local histogram
            for (String sTerm : dTerms.asTreeMap().keySet())
                // Update overall histogram (IDF)
                dTermsInDocs.increaseValue(sTerm, 1.0);
        }

        // For each text TF in overall TF struct
        for (String sTextTitle : hTFsPerDoc.keySet()) {
            Map<String, Double> hCurDocTF = hTFsPerDoc.get(sTextTitle).asTreeMap();
            // Init top terms distro
            Distribution<String> dTopTerms = new Distribution<>();
            // For every term
            for (String sTerm : hCurDocTF.keySet()) {
                // Calculate TF-IDF
//                double dTf = hCurDocTF.get(sTerm) / hCurDocTF.values().stream().mapToDouble(Double::doubleValue).sum();
//                double dIdf = Math.log(documents.size() / dTermsInDocs.getValue(sTerm));
//                double dTFIDF = dTf * dIdf;
                double dTFIDF = hCurDocTF.get(sTerm) * -Math.log10(dTermsInDocs.getValue(sTerm) / documents.size());

                if (dTFIDF > 0.0) {
                    // Update doc struct
                    dTopTerms.setValue(sTerm, dTFIDF);
                }
            }

            // Save top terms
            List<Pair<String, Double>> lTmp = new ArrayList<>();
            int iTermsToKeep = Math.round(dTopTerms.asTreeMap().size() * 0.05f);

            // Keep adding top items until we reach a number we want
            while (lTmp.size() < iTermsToKeep) {
                String sTopTerm = dTopTerms.getKeyOfMaxValue();
                lTmp.add(new Pair<>(sTopTerm, dTopTerms.getValue(sTopTerm)));
                // Remove from list of candidates
                dTopTerms.asTreeMap().remove(sTopTerm);
            }
            // Actually update cache of top terms
            mTopTermsPerDocument.put(sTextTitle, lTmp);
        }

        // DEBUG LINES
//        System.err.println("Optimized:\n" + utils.printIterable(mTopTermsPerDocument.entrySet(), "\n") + "\n\n");
        //////////////
    }

    @Override
    public List<Pair<String, Double>> getSortedDocumentTerms(String docName) {
        return mTopTermsPerDocument.get(docName);
    }
}
