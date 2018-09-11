package Database;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.opencsv.CSVReader;

/**
 * Class which contains the method to read in the CSV files and
 * import the data into the training_data database table.
 *
 * @author Alex Kelly
 */
public class ReadFiles {

    /**
     * Method to read in the training data and populate the training_data
     * table within the data database. Uses opencsv library, JAR file added
     * to dependencies. Rows are dumped into the database tables in batches
     * of 100 for higher efficiency.
     */
    public void readCSVTraining() {

        for (int year = 2008; year <= 2018; year++) {

            String csvFile = "/Users/alex/Documents/UoB-Summer-Term/results/training/atp_matches_" + year + ".csv";

            String insertSQL = "INSERT into training_data VALUES " +
                    "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            try (CSVReader reader = new CSVReader(new FileReader(csvFile), ',');
                 Connection conn = ConnectDB.connect();
                 PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

                String[] row;
                reader.readNext(); //skip headings
                int i = 0;
                int noOfCols = 50;
                while ((row = reader.readNext()) != null) {
                    for (String data : row) {
                        pstmt.setString((i % noOfCols) + 1, data);
                        if (++i % noOfCols == 0) {
                            pstmt.addBatch(); //add batch
                        }
                        if (i % (noOfCols * 100) == 0) {//insert when the batch getSize is 100
                            pstmt.executeBatch();
                        }
                    }
                }
                System.out.println("Data for year " + year + " loaded into training_data");
            } catch (Exception e) {
                System.err.println("Error loading data from year " + year + " into database: " + e);
                e.printStackTrace();
            }
        }
    }

//    /**
//     * Method to read in the test data and populate the test table
//     * within the data database. Uses opencsv library, JAR file added
//     * to dependencies.
//     *
//     * @param year the year of the results to be read in and stored
//     */
//    private void readCSVTest(int year) {
//
//        if (year < 2016 || year > 2018) {
//            throw new IllegalArgumentException();
//        }
//
//        String csvFile = "/Users/alex/Documents/UoB-Summer-Term/results/test/atp_matches_" + year + ".csv";
//
//        String insertSQL = "INSERT into test_data VALUES " +
//                "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//
//        try (CSVReader reader = new CSVReader(new FileReader(csvFile), ',');
//             Connection conn = ConnectDB.connect();
//             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
//
//            String[] row;
//            reader.readNext(); //skip headings
//            int i = 0;
//            int noOfCols = 50;
//            while((row = reader.readNext()) != null) {
//                for (String data : row) {
//                    pstmt.setString((i % noOfCols) + 1, data);
//                    if (++i % noOfCols == 0) {
//                        pstmt.addBatch(); //add batch
//                    }
//                    if (i % (noOfCols * 100) == 0) {//insert when the batch getSize is 100
//                        pstmt.executeBatch();
//                    }
//                }
//            }
//            System.out.println("Data for year " + year + " loaded into training_data");
//        }
//        catch (Exception e) {
//            System.err.println("Error loading data from year " + year + " into database: " + e);
//            e.printStackTrace();
//        }
//    }

}
