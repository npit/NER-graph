package entity_extractor;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class OpenCalaisExtractor implements EntityExtractor {
    private static final String CALAIS_URL = "https://api.thomsonreuters.com/permid/calais";

    private static final String outputFolder = "texts/output";
    private static final boolean enableCache = true;

    private final ArrayList<String> apiKeys;
    private File output;
    private HttpClient client;

    public OpenCalaisExtractor() {
        // Setup client
        this.client = new HttpClient();
        this.client.getParams().setParameter("http.useragent", "Calais Rest Client");

        // Setup api keys
        apiKeys = new ArrayList<>();
        apiKeys.add("***REMOVED***");

        // Setup cache/output folder
        this.output = new File(outputFolder);
    }

    private PostMethod createPostMethod() {
        PostMethod method = new PostMethod(CALAIS_URL);

        method.setRequestHeader("X-AG-Access-Token", apiKeys.get(0));   // Set mandatory parameters
        method.setRequestHeader("Content-Type", "text/raw");            // Set input content type
        method.setRequestHeader("outputformat", "application/json");    // Set response/output format

        return method;
    }

    private String doRequest(File file, PostMethod method) {
        try {
            int returnCode = client.executeMethod(method);
            if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                System.err.println("The Post method is not implemented by this URI");
                // still consume the response body
                method.getResponseBodyAsString();
            } else if (returnCode == HttpStatus.SC_OK) {
                System.out.println("File post succeeded: " + file);

                return saveResponse(file, method);
            } else {
                System.err.println("File post failed: " + file);
                System.err.println("Got code: " + returnCode);
                System.err.println("response: "+method.getResponseBodyAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }

        return null;
    }

    /**
     * Save an OpenCalais response to a file and return it as a string
     * @param file      File to save response for
     * @param method    Post method
     * @return          OpenCalais response string
     */
    private String saveResponse(File file, PostMethod method) {
        PrintWriter writer = null;
        String response = "";

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    method.getResponseBodyAsStream(), "UTF-8"));
            File out = new File(output, file.getName() + ".json");
            writer = new PrintWriter(new BufferedWriter(new FileWriter(out)));
            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
                response += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) try {writer.close();} catch (Exception ignored) {}
        }

        return response;
    }

    private String postFile(File file, PostMethod method) {
        method.setRequestEntity(new FileRequestEntity(file, null));
        return doRequest(file, method);
    }

    /**
     * Reads the specified file and returns it as a string
     * @param file  File to read
     * @return      String with file contents
     */
    private String getCachedResponse(File file) {
        String response = "";

        try {
            // Credit: http://stackoverflow.com/a/40299794
            response = new String(Files.readAllBytes(Paths.get(file.toString())));
        } catch (IOException e) {
            System.err.println("Error while reading cached file: " + e.getMessage());
        }
        return response;
    }

    /**
     * Parse the OpenCalais JSON response string into a ArrayList<ExtractedEntity> object
     * @param responseStr    Response string
     * @return              Text entities
     */
    private TextEntities getEntitiesFromOpenCalaisResponse(String responseStr) {
        TextEntities entities = new TextEntities();
        JSONObject obj = new JSONObject(responseStr);

        // Get the document text from the response
        String text = obj.getJSONObject("doc").getJSONObject("info").getString("document");
        entities.setText(text);

        // Get entities
        Iterator<String> tags = obj.keys();
        while(tags.hasNext()) {
            String tagID = tags.next();

            if (tagID.equals("doc"))
                continue;

            JSONObject tag = obj.getJSONObject(tagID);

            // Check that object has at least the basic properties that we need to continue
            if (!tag.has("_typeGroup") || !tag.has("name") || !tag.has("_type") || !tag.has("instances")) {
                continue;
            }

            // If object is not of type "entities", ignore it as we only want entities
            String typeGroup = tag.getString("_typeGroup");
            if (!typeGroup.equals("entities"))
                continue;

            // Skip tags that not for end user display
//            if (tag.getString("forenduserdisplay").equals("false")) {
//                continue;
//            }

            // Get all of this entity's instances
            JSONArray instances = tag.getJSONArray("instances");

            for (Object instance : instances) {
                // Check that the object is indeed a JSONObject before casting it
                if (!(instance instanceof JSONObject)) {
                    System.err.println("Instance is not a JSONObject! Skipping it...");
                    continue;
                }

                // Cast instance to a JSONObject to continue
                JSONObject i = (JSONObject)instance;

                if (!i.has("offset") || !i.has("length")) {
                    System.err.println("Instance does not have offset or length! Skipping it...");
                    continue;
                }

                // Initialize entity for this instance and add the required properties to it
                ExtractedEntity entity = new ExtractedEntity();

//                entity.setName(i.getString("exact"));
                entity.setName(tag.getString("name"));
                entity.setType(tag.getString("_type"));
                entity.setOffset(i.getInt("offset"));
                entity.setLength(i.getInt("length"));

                entities.addEntity(entity);
            }
        }

        return entities;
    }

    public TextEntities getEntities(File input) {
        // Before making request to OpenCalais, check that the file does not already exist
        String outputFilename = output.toString() + "/" + input.getName() + ".json";
        File outfile = new File(outputFilename);

        System.out.print("[OpenCalaisExtractor] Checking if OpenCalais response is cached... ");
        String response;
        if (outfile.isFile() && enableCache) {
            System.out.println("yes");
            response = getCachedResponse(outfile);
        } else {
            System.out.println("no");
            response = postFile(input, createPostMethod());
        }

        // Get entities from response and add filename as title
        TextEntities entities = getEntitiesFromOpenCalaisResponse(response);
        entities.setTitle(input.getName());

        return entities;
    }
}
