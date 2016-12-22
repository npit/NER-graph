import org.javatuples.Triplet;
import smile.clustering.GMeans;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClusterTest {
    private final static String csvPath = "MUC3_python/temp-500.csv";
//    private final static String csvPath = "out.csv";

    public static void main(String[] args) {
        ClusterTest ct = new ClusterTest();

        ct.start();
    }

    private void start() {
        // Hashmap to save the ID for each text
        Map<String, Integer> textMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            int[] textIds;

            // Create tuple arraylist to hold the comparison data to create data array later
            ArrayList<Triplet<Integer, Integer, Double>> comparisons = new ArrayList<>();

            // Get headers and ask the user which column he would like to use for clustering
            String[] headers = br.readLine().split(",");
//            int colToCluster = askForColumnID(headers);
            int colToCluster = 13;

            System.out.println("Using column with name: " + headers[colToCluster]);

            while ((line = br.readLine()) != null) {
                // Process the line
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

                    // Get the similarity of the two texts from the column
                    double similarityValue = Double.valueOf(fields[colToCluster]);

                    // Add a new triplet with the data to the arraylist
                    comparisons.add(new Triplet<>(textIds[0], textIds[1], similarityValue));
                } else {
                    System.err.println("Error... Not even 3 fields in CSV?");
                }
            }

            // Create the data array
            double[][] data = new double[textMap.size()][textMap.size()];

            for (Triplet<Integer, Integer, Double> t : comparisons) {
                // Get data from triplet
                int id1 = t.getValue0();
                int id2 = t.getValue1();
                double value = t.getValue2();

                // Add data from triplet to the array for clustering
                data[id1][id2] = value;
                data[id2][id1] = value;
            }

            // Make each text be completely the same with itself because it is but array is initialized with zeroes
//            for (int i = 0; i < data.length; i++) {
//                data[i][i] = 1.0;
//            }

            // Print data array
//            for (double[] aData : data) {
//                for (int j = 0; j < data.length; j++) {
//                    System.out.print(aData[j] + "\t");
//                }
//                System.out.println();
//            }

            GMeans clusters = new GMeans(data, 100);
            System.out.println("Clusters number: " + clusters.getNumClusters());
            int[] labels = clusters.getClusterLabel();
            int[] sizes = clusters.getClusterSize();

            // se poio cluster einai to kathe text (?????)
            System.out.println("Labels " + labels.length);

            // ta megethi tou kathe dataset
            System.out.println("Sizes " + sizes.length);
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
        while(id > columns.length - 1 || id == 0 || id == 1) {
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
            strings.set(map.get(s), s);
        }

        // Print list
        int strSize = strings.size();
        for (int i = 0; i < strSize; i++) {
            System.out.println(i + ". " + strings.get(i));
        }
    }
}
