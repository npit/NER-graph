package csv_export;

import utils.Methods;

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

        try {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
//            writer.println("sep=" + separator);

            // Print header for text titles
            writer.print("Text 1,Text 2,");

            List<String> comparisons = new ArrayList<>();

            // Gather names of comparisons that were made
            if (Methods.isEnabled(Methods.N_GRAMS))
                comparisons.add("n-gram graph");

            if (Methods.isEnabled(Methods.WORD_GRAPHS))
                comparisons.add("word graph");

            if (Methods.isEnabled(Methods.PLACEHOLDER)) {
                for (String ph : placeholders) {
                    comparisons.add("PH (" + ph + ")");
                }
            }

            if (Methods.isEnabled(Methods.PLACEHOLDER_SS)) {
                for (String ph : placeholders) {
                    comparisons.add("PHSS (" + ph + ")");
                }
            }

            if (Methods.isEnabled(Methods.RANDOM))
                comparisons.add("rand");

            if (Methods.isEnabled(Methods.COSINE))
                comparisons.add("cosine");

            if (Methods.isEnabled(Methods.PLACEHOLDER_EXTRA_WEIGHT)) {
                for (String ph : placeholders) {
                    comparisons.add("PHEW (" + ph + ")");
                }
            }

            // Create headers with graph similarity measures for each comparison
            sb = new StringBuilder();

            for (String s : comparisons) {
                // Value similarity header
                sb.append(s);
                sb.append(" val");
                sb.append(separator);

                // Containment similarity header
                sb.append(s);
                sb.append(" cont");
                sb.append(separator);

                // Size similarity header
                sb.append(s);
                sb.append(" size");
                sb.append(separator);

                // Normalized value similarity header
                sb.append(s);
                sb.append(" NVS");
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
                    sb.append(res.getNVS());
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
