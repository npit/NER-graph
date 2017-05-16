package utils.tf_idf;

import gr.demokritos.iit.conceptualIndex.structs.Distribution;

public class CosineSimilarityOptimized {
    /**
     * Compare two term vectors with cosine similarity. Vectors are assumed to be the same size,
     * or bad things will happen.
     *
     * @param docVector1 Vector 1
     * @param docVector2 Vector 2
     * @return Cosine Similarity
     */
    public double cosineSimilarity(Distribution<String> docVector1, Distribution<String> docVector2) {
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        double cosineSimilarity;

        for (String s : docVector1.asTreeMap().keySet()) {
            dotProduct += docVector1.getValue(s) * docVector2.getValue(s);
            magnitude1 += Math.pow(docVector1.getValue(s), 2);
            magnitude2 += Math.pow(docVector2.getValue(s), 2);
        }

        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);

        if (magnitude1 != 0.0 && magnitude2 != 0.0) {
            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
        } else {
            cosineSimilarity = 0.0;
        }

        return cosineSimilarity;
    }
}
