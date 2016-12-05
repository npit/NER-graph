import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class ClusterTest {
    private final static String csvPath = "MUC3_python/temp.csv";

    public static void main(String[] args) {
        ClusterTest ct = new ClusterTest();

        ct.start();
    }

    private void start() {
        System.out.println("clustering test!");

        // Hashmap to save the ID for each text
        Map<String, Integer> textMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            int[] textIds;

            // Skip headers (this var. should be used later to select which comparison to make clusters with)
            String headers = br.readLine();

            while ((line = br.readLine()) != null) {
                // process the line.
                String[] fields = line.split(",");

                if (fields.length > 2) {
                    // Array to keep the text IDs
                    textIds = new int[2];

                    // Get the ID of each text title (pro tip: they are 2)
                    for (int i = 0; i < 2; i++) {
                        // Get text title
                        String textTitle = fields[i];

                        // If the text does not have an ID, add it to hashmap with a new one
                        if (!textMap.containsKey(textTitle)) {
                            textMap.put(textTitle, textMap.size());
                        }

                        // Get the ID of the text
                        textIds[i] = textMap.get(textTitle);
                    }

                } else {
                    System.err.println("Error... Not even 3 fields in CSV?");
                }
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
