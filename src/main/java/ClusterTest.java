import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
            String[] headers = br.readLine().split(",");
            int colToCluster = askForColumnID(headers);
//            int colToCluster = 13;

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

            // Print the list of texts and their IDs
            printKeys(textMap);

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    private int askForColumnID(String[] columns) {
        int id = columns.length;

        // Print columns
        for (int i = 2; i < id; i++) {
            System.out.println(i + ") " + columns[i]);
        }

        // Ask for ID

        while(id > columns.length - 1) {
            System.out.print("Enter number of column to cluster: ");
            id = new Scanner(System.in).nextInt();
        }

        return id;
    }

    /**
     * Print the list of texts and their IDs (sorted by ID)
     * @param map   The map of text titles & IDs
     */
    private void printKeys(Map<String, Integer> map) {
        // Create "empty" arraylist
        ArrayList<String> strings = new ArrayList<>();

        for (int i = 0 ; i < map.size(); i++) {
            strings.add("");
        }

        // Add each item to its index
        for (String s : map.keySet()) {
            System.out.println("adding " + s + " to index " + map.get(s));
            strings.set(map.get(s), s);
        }

        // Print list
        int strSize = strings.size();
        for (int i = 0; i < strSize; i++) {
            System.out.println(i + ". " + strings.get(i));
        }
    }
}
