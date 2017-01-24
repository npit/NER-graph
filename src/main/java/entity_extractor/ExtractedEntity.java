package entity_extractor;

/**
 * An extracted entity from a text
 */
@SuppressWarnings("WeakerAccess")
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

    /**
     * Return a hash string for this entity's name. It is assumed that same name == same entity IRL
     * @return  Hash string
     */
    public String getHash() {
        return "" + name.hashCode();
    }

    @Override
    public int hashCode() {
        // Return hashcode of name
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // Assume that same name == same entity
        if (!(obj instanceof ExtractedEntity))
            return false;
        if (obj == this)
            return true;

        ExtractedEntity rhs = (ExtractedEntity) obj;

        return (rhs.getName().equals(this.getName()));
    }

    @Override
    public String toString() {
        return "[Entity] name: " + this.name + " | type: " + this.type + " | " +
                "offset: " + this.offset + " | length: " + this.length;
    }
}
