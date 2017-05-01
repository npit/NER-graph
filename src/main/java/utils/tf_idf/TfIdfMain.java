package utils.tf_idf;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Mubin Shrestha
 */
public class TfIdfMain {

    /**
     * Main method
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String args[]) throws FileNotFoundException, IOException
    {
        DocumentParser dp = new DocumentParser();
        dp.parseFiles("D:\\FolderToCalculateCosineSimilarityOf"); // give the location of source file
        dp.tfIdfCalculator(); //calculates tfidf
        dp.getCosineSimilarity(); //calculates cosine similarity
    }
}
