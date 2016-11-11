package entity_extractor;

import java.io.File;

public interface EntityExtractor {
    public void setApiKey(String apiKey);

    public void setOutputDir(File dir);

    public TextEntities getEntities(File input);
}
