package utils.tf_idf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to read documents
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
                }
                String[] tokenizedTerms = sb.toString().replaceAll("[\\W&&[^\\s]]", "").split("\\W+");   //to get individual terms
                for (String term : tokenizedTerms) {
                    if (!allTerms.contains(term)) {  //avoid duplicate entry
                        allTerms.add(term);
                    }
                }

                docNames.add(f.getName());
                termsDocsArray.add(tokenizedTerms);
            }
        }
    }

    /**
     * Method to create termVector according to its tfidf score.
     */
    public void tfIdfCalculator() {
        double tf;      //term frequency
        double idf;     //inverse document frequency
        double tfidf;   //term frequency inverse document frequency

        // For each document, calculate its vector
        for (int i = 0; i < termsDocsArray.size(); i++) {
            String[] docTermsArray = termsDocsArray.get(i);
            double[] tfidfvectors = new double[allTerms.size()];
            int count = 0;
            for (String terms : allTerms) {
                tf = new TfIdf().tfCalculator(docTermsArray, terms);
                idf = new TfIdf().idfCalculator(termsDocsArray, terms);
                tfidf = tf * idf;
                tfidfvectors[count] = tfidf;
                count++;
            }
            tfidfDocsVector.add(tfidfvectors);  //storing document vectors;
        }
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
