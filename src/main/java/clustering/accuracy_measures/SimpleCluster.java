package clustering.accuracy_measures;

import java.util.ArrayList;

/**
 * Created by Leo on 2017-01-17.
 */
public class SimpleCluster {
    private ArrayList<Integer> texts;

    public SimpleCluster() {
        texts = new ArrayList<>();
    }

    public void addText(Integer textIndex) {
        texts.add(textIndex);
    }

    public void addAllTexts(ArrayList<Integer> textIndexes) {
        texts.addAll(textIndexes);
    }

    public ArrayList<Integer> getTexts() {
        return texts;
    }
}
