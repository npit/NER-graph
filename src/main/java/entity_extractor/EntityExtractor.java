package entity_extractor;

import java.io.File;
import java.util.ArrayList;

public interface EntityExtractor {
    TextEntities getEntities(File input);
    public void setApiKeys(ArrayList<String> l);
}
