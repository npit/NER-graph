package utils.tf_idf;

import entity_extractor.TextEntities;
import org.javatuples.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class to read documents (modified)
 *
 * @author Mubin Shrestha
 */
public class DocumentParser {

    //This variable will hold all terms of each document in an array.
    private List<String> docNames = new ArrayList<>();
    private List<String[]> termsDocsArray = new ArrayList<>();
    private List<String> allTerms = new ArrayList<>(); //to hold all terms
    private List<double[]> tfidfDocsVector = new ArrayList<>();

    /**
     * Method to read files and store in array.
     *
     * @param filePath : source file path
     * @throws IOException
     */
    public void parseFiles(String filePath) throws IOException {
        File[] allfiles = new File(filePath).listFiles();
        BufferedReader in = null;
        for (File f : allfiles) {
            if (f.isFile()) {
                in = new BufferedReader(new FileReader(f));
                StringBuilder sb = new StringBuilder();
                String s = null;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append(" ");
                }

                // Get text, make it upper case and split into terms
                String text = sb.toString().toUpperCase();
                String[] tokenizedTerms = text.replaceAll("[\\W&&[^\\s]]", " ").split("\\W+");   //to get individual terms

                for (String term : tokenizedTerms) {
                    if (!allTerms.contains(term)) {  //avoid duplicate entry
                        allTerms.add(term);
                    }
                }

                docNames.add(f.getName());

                int termsNum = tokenizedTerms.length;
                String[] tokenizedNormalizedTerms = new String[termsNum];
                for (int i = 0; i < termsNum; i++) {
                    tokenizedNormalizedTerms[i] = tokenizedTerms[i].toUpperCase();
                }

                termsDocsArray.add(tokenizedNormalizedTerms);
            }
        }

        // Run tfIdf
        tfIdfCalculator();
    }

    public void parseFiles(List<TextEntities> documents) {
        for (TextEntities doc : documents) {
            String text = doc.getText().toUpperCase();
            String[] tokenizedTerms = text.replaceAll("[\\W&&[^\\s]]", " ").split("\\W+");   //to get individual terms

            for (String term : tokenizedTerms) {
                if (!allTerms.contains(term)) {  //avoid duplicate entry
                    allTerms.add(term);
                }
            }

            docNames.add(doc.getTitle());

            int termsNum = tokenizedTerms.length;
            String[] tokenizedNormalizedTerms = new String[termsNum];
            for (int i = 0; i < termsNum; i++) {
                tokenizedNormalizedTerms[i] = tokenizedTerms[i].toUpperCase();
            }

            termsDocsArray.add(tokenizedNormalizedTerms);
        }

        // Run tfIdf
        tfIdfCalculator();
    }

    /**
     * Method to create termVector according to its tfidf score.
     */
    private void tfIdfCalculator() {
        TFIDFCalculator calculator = new TFIDFCalculator();
        double tfidf;   //term frequency inverse document frequency

        // Create values for TFIDFCalculator class (lists)
        List<List<String>> documents = new ArrayList<>();
        for (String[] docTerms : termsDocsArray) {
            documents.add(Arrays.asList(docTerms));
        }

        // For each document, calculate its vector
        for (int docIndex = 0; docIndex < termsDocsArray.size(); docIndex++) {
            String[] docTermsArray = termsDocsArray.get(docIndex);
            List<String> docTermsList = Arrays.asList(docTermsArray);

            double[] tfidfvectors = new double[allTerms.size()];

            int count = 0;
            for (String term : allTerms) {
                tfidf = calculator.tfIdf(docTermsList, documents, term);
                tfidfvectors[count] = tfidf;
                count++;
            }

            tfidfDocsVector.add(tfidfvectors);  //storing document vectors;
        }
    }

    /**
     * Get a sorted list of the top terms of a document, ranked by TF-IDF
     *
     * @param docName
     * @return
     */
    public List<Pair<String, Double>> getSortedDocumentTerms(String docName) {
        // Check that the requested document exists
        if (docNames.contains(docName)) {
            // Get document index and
            int docIndex = docNames.indexOf(docName);
            double[] tfIdfVector = tfidfDocsVector.get(docIndex);

            // Create the list of pairs
            List<Pair<String, Double>> terms = new ArrayList<>();

            int termsLen = allTerms.size();
            for (int i = 0; i < termsLen; i++) {
                String term = allTerms.get(i);
                double termRank = tfIdfVector[i];

                terms.add(new Pair<>(term, termRank));
            }

            // Remove words with 0 rank value
            terms.removeIf(obj -> obj.getValue1().equals(0.0));

            // Sort the list with comparator which compares the doubles
            terms.sort((o1, o2) -> {
                Double d1 = o1.getValue1();
                Double d2 = o2.getValue1();

                return d1.compareTo(d2);
            });

            // Reverse the list in order to make the top terms be first
            Collections.reverse(terms);

            // Keep top 5% of words
            terms = terms.subList(0, Math.round(terms.size() * 0.05f));

            return terms;
        }

        return null;
    }

    /**
     * Method to calculate cosine similarity between all the documents.
     */
    public void getCosineSimilarity() {
        for (int i = 0; i < tfidfDocsVector.size(); i++) {
            for (int j = 0; j < tfidfDocsVector.size(); j++) {
                System.out.println("between " + i + " and " + j + "  =  "
                        + new CosineSimilarity().cosineSimilarity
                        (
                                tfidfDocsVector.get(i),
                                tfidfDocsVector.get(j)
                        )
                );
            }
        }
    }
}
