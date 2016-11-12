package entity_extractor;

/**
 * An extracted entity from a text
 */
public class ExtractedEntity {
    private String name;
    private String type;
    private int offset;
    private int length;

    public ExtractedEntity() {
    }

    public ExtractedEntity(String name, String type, int offset, int length) {
        this.name = name;
        this.type = type;
        this.offset = offset;
        this.length = length;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
