package csv_export;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CSVExporter {
    private static String separator = ",";

    @SuppressWarnings("SameParameterValue")
    public static void exportCSV(String filename, List<String> placeholders, List<ComparisonContainer> results) {
        Logger LOGGER = Logger.getLogger("NamedEntityGraph");
        StringBuilder sb;

        try{
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
//            writer.println("sep=" + separator);

            // Print header for text titles
            writer.print("Text 1, Text 2,");

            List<String> comparisons = new ArrayList<>();

            // Gather names of comparisons that were made
            comparisons.add("n-gram graph");
            comparisons.add("word graph");
            for (String ph : placeholders) {
                comparisons.add("PH (" + ph + ")");
            }
            for (String ph : placeholders) {
                comparisons.add("PHSS (" + ph + ")");
            }
            comparisons.add("rand");

            // Create headers with graph similarity measure
            sb = new StringBuilder();

            for (String s : comparisons) {
                sb.append(s);
                sb.append(" val");
                sb.append(separator);
                sb.append(s);
                sb.append(" cont");
                sb.append(separator);
                sb.append(s);
                sb.append(" size");
                sb.append(separator);
            }

            sb.delete(sb.length() - separator.length(), sb.length());
            writer.println(sb.toString());

            // Print results
            for (ComparisonContainer cont : results) {
                sb = new StringBuilder();

                // Compared text filenames
                sb.append(cont.getText1());
                sb.append(separator);
                sb.append(cont.getText2());
                sb.append(separator);

                // Results
                for (ComparisonResult res : cont.getResults()) {
                    sb.append(res.getValueSim());
                    sb.append(separator);
                    sb.append(res.getContainmentSim());
                    sb.append(separator);
                    sb.append(res.getSizeSim());
                    sb.append(separator);
                }

                // Delete last separator of line
                sb.delete(sb.length() - separator.length(), sb.length());

                writer.println(sb.toString());
            }

            writer.close();

            LOGGER.log(Level.INFO, "CSV file written successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error writing CSV file: " + Arrays.toString(e.getStackTrace()));
        }
    }
}
