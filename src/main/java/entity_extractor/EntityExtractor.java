package entity_extractor;

import java.io.File;

public interface EntityExtractor {
    void setApiKey(String apiKey);

    void setOutputDir(File dir);

    TextEntities getEntities(File input);
}
