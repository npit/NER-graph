package entity_extractor;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Entities extracted from a text
 */
public class TextEntities {
    private final static String wordSeparator = " ";
    private ArrayList<ExtractedEntity> entities;
    private String text;

    public TextEntities() {
        this.entities = new ArrayList<>();
    }

    public void addEntity(ExtractedEntity e) {
        this.entities.add(e);
    }

    public ArrayList<ExtractedEntity> getEntities() {
        return entities;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void printEntities() {
        for (ExtractedEntity e : entities) {
            System.out.println(e);
            System.out.println("| index: " + getEntityIndex(e.getOffset()));
        }

        System.out.println("Number of words in text: " + getNumberOfWordsInText());
    }

    private int getNumberOfWordsInText() {
        StringTokenizer st = new StringTokenizer(text, wordSeparator);

        return st.countTokens();
    }

    private int getEntityIndex(int offset) {
        // Get text until the entity
        String textUntilEntity = text.substring(0, offset);

        // Count words in the text
        StringTokenizer st = new StringTokenizer(textUntilEntity, wordSeparator);

        return st.countTokens();
    }
}
