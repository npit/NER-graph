package entity_extractor;

import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentWordGraph;

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

    public GraphCache() {
        wordGraphPH = new HashMap<>();
        wordGraphPHSS = new HashMap<>();
    }

    public void calculateGraphs(TextEntities te, List<String> placeholders) {
        // N-gram graph for the normal text
        nGramNormalText = new DocumentNGramGraph();
        nGramNormalText.setDataString(te.getText());

        // Word graph for the normal text
        wordGraphNormalText = new DocumentWordGraph();
        wordGraphNormalText.setDataString(te.getText());

        for (String ph : placeholders) {
            // Word graph for placeholder method
            DocumentWordGraph g = new DocumentWordGraph();
            g.setDataString(te.getEntityTextWithPlaceholders(ph));
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

    public DocumentNGramGraph getnGramNormalText() {
        return nGramNormalText;
    }

    public DocumentWordGraph getWordGraphNormalText() {
        return wordGraphNormalText;
    }

    public DocumentWordGraph getWordGraphPH(String placeholder) {
        return wordGraphPH.get(placeholder);
    }

    public DocumentWordGraph getWordGraphPHSS(String placeholder) {
        return wordGraphPHSS.get(placeholder);
    }

    public DocumentWordGraph getWordGraphRand() {
        return wordGraphRand;
    }
}
