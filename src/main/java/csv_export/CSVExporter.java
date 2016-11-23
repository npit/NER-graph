package csv_export;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CSVExporter {
    private static String separator = ",";

    @SuppressWarnings("SameParameterValue")
    public static void exportCSV(String filename, List<String> placeholders, List<ComparisonContainer> results) {
        Logger LOGGER = Logger.getLogger("NamedEntityGraph");

        try{
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            writer.println("sep=" + separator);

            //todo: print headers

            // Print results to file
            StringBuilder sb;
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
