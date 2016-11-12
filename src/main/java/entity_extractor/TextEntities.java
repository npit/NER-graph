package entity_extractor;

import java.util.ArrayList;

/**
 * Entities extracted from a text
 */
public class TextEntities {
    private ArrayList<ExtractedEntity> entities;
    private String text;

    public TextEntities(String text) {
        this.text = text;

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
}
