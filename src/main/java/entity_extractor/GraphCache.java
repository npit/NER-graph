package entity_extractor;

import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentWordGraph;
import org.javatuples.Pair;
import utils.tf_idf.DocumentParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class GraphCache {
    private DocumentNGramGraph nGramNormalText;
    private DocumentWordGraph wordGraphNormalText;
    private Map<String, DocumentWordGraph> wordGraphPH;
    private Map<String, DocumentWordGraph> wordGraphPHSS;
    private DocumentWordGraph wordGraphRand;

    private DocumentParser dp;
    private TextEntities text;

    public GraphCache(TextEntities text, DocumentParser dp) {
        this.wordGraphPH = new HashMap<>();
        this.wordGraphPHSS = new HashMap<>();
        this.nGramNormalText = null;
        this.wordGraphNormalText = null;
        this.wordGraphRand = null;

        this.dp = dp;
        this.text = text;
    }

    /**
     * Using the DocumentParser, get the text's words, sorted from highest
     * to lowest ranking
     *
     * @param te Text Entities object
     * @return List of strings, which are the terms in the ranked order
     */
    private List<String> getTopTerms(TextEntities te) {
        List<Pair<String, Double>> termsWithRanks = dp.getSortedDocumentTerms(te.getTitle());

        List<String> terms = new ArrayList<>();

        if (termsWithRanks != null && !termsWithRanks.isEmpty()) {
            for (Pair<String, Double> p : termsWithRanks) {
                terms.add(p.getValue0());
            }
        }

        return terms;
    }

    /**
     * Calculate graphs for all methods and save them. Takes up a lot of memory...
     *
     * @param te           TextEntities object for the text
     * @param placeholders Placeholders to use for methods that replace words with placeholders
     */
    public void calculateGraphs(TextEntities te, List<String> placeholders) {
        // Get top terms for this text
        List<String> topTerms = getTopTerms(te);

        // N-gram graph for the normal text
        nGramNormalText = new DocumentNGramGraph();
        nGramNormalText.setDataString(te.getText());

        // Word graph for the normal text
        wordGraphNormalText = new DocumentWordGraph();
        wordGraphNormalText.setDataString(te.getText());

        for (String ph : placeholders) {
            // Word graph for placeholder method
            DocumentWordGraph g = new DocumentWordGraph();

            // If top terms exist, use method which uses them
            if (topTerms != null && !topTerms.isEmpty()) {
                g.setDataString(te.getEntityTextWithPlaceholders(ph, topTerms));
            } else {
                g.setDataString(te.getEntityTextWithPlaceholders(ph));
            }

            wordGraphPH.put(ph, g);

            // Word graph for placeholder same size method
            g = new DocumentWordGraph();
            g.setDataString(te.getEntityTextWithPlaceholderSameSize(ph));
            wordGraphPHSS.put(ph, g);
        }

        // Word graph for random method
        wordGraphRand = new DocumentWordGraph();
        wordGraphRand.setDataString(te.getEntityTextWithRandomWord());
    }

    /**
     * Return an n-gram graph of the normal text. If it does not exist, create it and return it
     *
     * @return N-gram graph
     */
    public DocumentNGramGraph getnGramNormalText() {
        if (nGramNormalText == null) {
            DocumentNGramGraph g = new DocumentNGramGraph();
            g.setDataString(text.getText());

            return g;
        } else {
            return nGramNormalText;
        }
    }

    /**
     * Return a word graph of the normal text. If it does not exist, create it and return it
     *
     * @return Word graph
     */
    public DocumentWordGraph getWordGraphNormalText() {
        if (wordGraphNormalText == null) {
            DocumentWordGraph g = new DocumentWordGraph();
            g.setDataString(text.getText());

            return g;
        } else {
            return wordGraphNormalText;
        }
    }

    /**
     * Return a word graph of the placeholder method text. If it does not exist, create it and return it
     *
     * @param placeholder Placeholder to use in text
     * @return Word graph
     */
    public DocumentWordGraph getWordGraphPH(String placeholder) {
        DocumentWordGraph g = wordGraphPH.get(placeholder);

        if (g == null) {
            g = new DocumentWordGraph();

            // Get top terms for this text
            List<String> topTerms = getTopTerms(text);

            // If top terms exist, use method which uses them
            if (topTerms != null && !topTerms.isEmpty()) {
                g.setDataString(text.getEntityTextWithPlaceholders(placeholder, topTerms));
            } else {
                g.setDataString(text.getEntityTextWithPlaceholders(placeholder));
            }
        }

        return g;
    }

    /**
     * Return a word graph of the placeholder same size method text. If it does not exist, create it and return it
     *
     * @param placeholder Placeholder to use in text
     * @return Word graph
     */
    public DocumentWordGraph getWordGraphPHSS(String placeholder) {
        DocumentWordGraph g = wordGraphPHSS.get(placeholder);

        if (g == null) {
            g = new DocumentWordGraph();
            g.setDataString(text.getEntityTextWithPlaceholderSameSize(placeholder));
        }

        return g;
    }

    /**
     * Return a word graph of the random method text. If it does not exist, create it and return it
     *
     * @return Word graph
     */
    public DocumentWordGraph getWordGraphRand() {
        if (wordGraphRand == null) {
            DocumentWordGraph g = new DocumentWordGraph();
            g.setDataString(text.getEntityTextWithRandomWord());

            return g;
        } else {
            return wordGraphRand;
        }
    }
}
