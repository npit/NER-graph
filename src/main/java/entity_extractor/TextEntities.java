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
            System.out.print(e);
            System.out.println(" | index: " + getEntityIndex(e.getOffset()));
        }

        System.out.println("Number of words in text: " + getNumberOfWordsInText());
    }

    private String getStringFromArrayList(ArrayList<String> stringList) {
        StringBuilder sb = new StringBuilder();
        for (String word : stringList) {
            sb.append(word);
            sb.append(wordSeparator);
        }

        return sb.toString();
    }

    /**
     * Return a string that is the original text, with every word that is not an entity replaced by a dummy word
     * @param dummyWord Word to replace non-entity words with
     * @return          Text with non-entity words replaced by the dummy word
     */
    public String getEntityTextWithDummyWord(String dummyWord) {
        int wordsNum = this.getNumberOfWordsInText();

        // Create string that is the same number of words as original text but all words are the dummy word
        ArrayList<String> entityTextWords = new ArrayList<>(wordsNum);


        for (int i = 0; i < wordsNum; i++) {
            entityTextWords.add(i, dummyWord);
        }

        // Replace words that should be entities with their entity names
        for (ExtractedEntity e : entities) {
            int index = getEntityIndex(e.getOffset());
            String entityText = e.getName();

            entityTextWords.set(index, entityText);
        }

        // Create string from the array list
        return getStringFromArrayList(entityTextWords);
    }

    /**
     * Return a string that is the original text, with every non-entity word replaced by a dummy word of the same size.
     * The dummy word is repeated multiple times until its length exceeds that of the original word and is then trimmed
     * to be the exact size of the original word.
     * @param dummyWord Word to replace non-entity words with
     * @return          The text in the described form
     */
    public String getEntityTextWithDummyWordSameSize(String dummyWord) {
        //todo
        return "";
    }

    /**
     * Return a string that is the original text with every non-entity word replaced by a random word of the same size
     * @return  The text in the described form
     */
    public String getEntityTextWithRandomWord() {
        //todo
        return "";
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
