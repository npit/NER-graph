package entity_extractor;

import java.io.File;

public interface EntityExtractor {
    TextEntities getEntities(File input);
}
