package entity_extractor;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Entities extracted from a text
 */
@SuppressWarnings("WeakerAccess")
public class TextEntities {
    private final static String wordSeparator = " ";
    private final static String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final ArrayList<ExtractedEntity> entities;
    private String text;
    private String title;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void printEntities() {
        for (ExtractedEntity e : entities) {
            System.out.print(e);
            System.out.println(" | index: " + getEntityIndex(e.getOffset()));
        }

        System.out.println("Number of words in text: " + getNumberOfWordsInText());
    }

    /**
     * Join an array list of strings into a single string, each string separated from each other with the wordSeparator
     * @param stringList    List of strings
     * @return              List of strings joined to a single string
     */
    private String getStringFromArrayList(ArrayList<String> stringList) {
        StringBuilder sb = new StringBuilder();
        for (String word : stringList) {
            sb.append(word);
            sb.append(wordSeparator);
        }

        return sb.toString();
    }

    /**
     * Return a string that is the original text, with every word that is not an entity replaced by a placeholder
     * @param placeholder   Word to replace non-entity words with
     * @return              Text with non-entity words replaced by the placeholder
     */
    public String getEntityTextWithPlaceholders(String placeholder) {
        int wordsNum = this.getNumberOfWordsInText();

        // Create string that is the same number of words as original text but all words are the placeholder word
        ArrayList<String> entityTextWords = new ArrayList<>(wordsNum);

        for (int i = 0; i < wordsNum; i++) {
            entityTextWords.add(i, placeholder);
        }

        // Replace words that should be entities with their entity names
        for (ExtractedEntity e : entities) {
            int index = getEntityIndex(e.getOffset());

            entityTextWords.set(index, e.getHash());
        }

        // Create string from the array list
        return getStringFromArrayList(entityTextWords);
    }

    /**
     * Return a string that is the original text, with every non-entity word replaced by a placeholder of the same size.
     * The placeholder word is repeated multiple times until its length exceeds that of the original word and is then
     * trimmed to be the exact size of the original word.
     * @param placeholder   Word to replace non-entity words with
     * @return              The text in the described form
     */
    public String getEntityTextWithPlaceholderSameSize(String placeholder) {
        int numOfWords = this.getNumberOfWordsInText();

        // Create array with the original text's words
        StringTokenizer st = new StringTokenizer(this.text, wordSeparator);
        ArrayList<String> words = new ArrayList<>(numOfWords);


        int tokenIndex = 0;
        while(st.hasMoreTokens()) {
            words.add(tokenIndex++, st.nextToken());
        }

        // Turn all words into placeholder words
        for (int i = 0; i < numOfWords; i++) {
            // Get the word to replace and its length
            String word = words.get(i);
            int wordLen = word.length();

            // Create word to replace it
            String newWord = "";

            do {
                newWord += placeholder;
            } while (newWord.length() <= wordLen);

            newWord = newWord.substring(0, wordLen);

            words.set(i, newWord);
        }

        // Add entity names back to the text
        for (ExtractedEntity e : this.entities) {
            int entityIndex = this.getEntityIndex(e.getOffset());

            words.set(entityIndex, e.getHash());
        }

        return getStringFromArrayList(words);
    }

    /**
     * Return a string that is the original text with every non-entity word replaced by a random word of the same size
     * @return  The text in the described form
     */
    public String getEntityTextWithRandomWord() {
        Random r = new Random();
        int alphabetLength = alphabet.length();
        int numOfWords = this.getNumberOfWordsInText();

        // Create array with the original text's words
        StringTokenizer st = new StringTokenizer(this.text, wordSeparator);
        ArrayList<String> words = new ArrayList<>(numOfWords);


        int tokenIndex = 0;
        while(st.hasMoreTokens()) {
            words.add(tokenIndex++, st.nextToken());
        }

        // Turn all words into words with random letters
        for (int i = 0; i < numOfWords; i++) {
            // Get the word to replace and its length
            String word = words.get(i);
            int wordLen = word.length();

            // Create word to replace it
            String newWord = "";

            do {
                // Add a random character from the alphabet to the word
                newWord += alphabet.charAt(r.nextInt(alphabetLength));
            } while (newWord.length() <= wordLen);

            newWord = newWord.substring(0, wordLen);

            words.set(i, newWord);
        }

        // Add entity names back to the text
        for (ExtractedEntity e : this.entities) {
            int entityIndex = this.getEntityIndex(e.getOffset());

            words.set(entityIndex, e.getHash());
        }

        return getStringFromArrayList(words);
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
