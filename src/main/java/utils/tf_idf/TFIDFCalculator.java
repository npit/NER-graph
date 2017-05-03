package utils.tf_idf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Source: https://gist.github.com/guenodz/d5add59b31114a3a3c66
 *
 * @author Mohamed Guendouz
 */
public class TFIDFCalculator {
    Map<String, Double> idfCache;

    public TFIDFCalculator() {
        idfCache = new HashMap<>();
    }

    /**
     * @param doc  list of strings
     * @param term String represents a term
     * @return term frequency of term in document
     */
    public double tf(List<String> doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }

        return result / doc.size();
    }

    /**
     * @param docs list of list of strings represents the dataset
     * @param term String represents a term
     * @return the inverse term frequency of term in documents
     */
    public double idf(List<List<String>> docs, String term) {
        if (idfCache.containsKey(term)) {
            // If the result is in the cache, get its IDF from there
            return idfCache.get(term);
        } else {
            // Calculate the IDF
            double n = 0;
            for (List<String> doc : docs) {
                for (String word : doc) {
                    if (term.equalsIgnoreCase(word)) {
                        n++;
                        break;
                    }
                }
            }

            double idfValue = Math.log(docs.size() / n);

            idfCache.put(term, idfValue);
            return idfValue;
        }
    }

    /**
     * @param doc  a text document
     * @param docs all documents
     * @param term term
     * @return the TF-IDF of term
     */
    public double tfIdf(List<String> doc, List<List<String>> docs, String term) {
        return tf(doc, term) * idf(docs, term);
    }
}
