import entity_extractor.HttpClientCalaisPost;

import java.util.ArrayList;

public class NamedEntityGraph {
    public static void main(String[] args) {
        // Main variables
        String input = "texts/input";
        String output = "texts/output";

        ArrayList<String> apiKeys = new ArrayList<String>();

        apiKeys.add("***REMOVED***");

        HttpClientCalaisPost httpClientPost = new HttpClientCalaisPost(input, output, apiKeys.get(0));
        httpClientPost.run();
    }
}
