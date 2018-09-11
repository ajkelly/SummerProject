package Database;

import Utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Contains the methods to create the test data table
 * and add the relevant data to it, as well as deleting
 * the test data from the training table.
 * Final database class to be called, once successfully
 * ran, data is ready for the network.
 *
 * @author Alex Kelly
 */
public class FinaliseDatabase {

    /**
     * Method that separates the training and test data into separate
     * tables. Training data is between the years 2008-2016 and test
     * data is 2017-2018.
     */
    public void addTestData() {

        //transfers 2017 and 2018 entries to test_data and deletes them from training_data
        String updateSQL = "CREATE TABLE test_data AS SELECT * FROM training_data WHERE year = '2017' OR year = '2018';";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            pstmt.executeUpdate();
            System.out.println("test_data table created");
            System.out.println("data from years 2017 and 2018 successfully added to test_data");

        } catch (SQLException e) {
            System.err.println("Error adding to test_data: " + e.getMessage());
            e.printStackTrace();
        }

        deleteTestDataFromTrainingData();
        System.out.println("\n**********************");
        System.out.println("  DATABASE FINALISED  ");
        System.out.println("**********************\n");
    }

    /**
     * Helper method to remove the data which has been added to test_data
     * from the training_data table.
     */
    private void deleteTestDataFromTrainingData() {

        String deleteSQL = "DELETE FROM training_data WHERE year = '2016' OR year = '2017' OR year = '2018';";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {

            pstmt.executeUpdate();
            System.out.println("test data successfully removed from training_data table");

        } catch (SQLException e) {
            System.err.println("Error deleting from training_data: " + e.getMessage());
            e.printStackTrace();
        }
    }


//    /**
//     * Method to create winner and loser tables in the database with an output
//     * column so the network can be supervised.
//     *
//     * @param tableName the existing table - training or test data - to take the
//     *                  inputs from and separate into winner and loser entries
//     * @param winnerOrLoser the table to create - winner for winners details, loser
//     *                      for the losers details
//     */
//    public void createWinnerAndLoserTables(String tableName, String winnerOrLoser) {
//
//        if (!(winnerOrLoser.equals("winner") || winnerOrLoser.equals("loser"))) {
//            throw new IllegalArgumentException();
//        }
//
//        if (!(tableName.equals("training_data") || tableName.equals("test_data"))) {
//            throw new IllegalArgumentException();
//        }
//
//        String wl;
//        if (winnerOrLoser.equals("winner")) {
//            wl = "loser";
//        } else wl = "winner";
//
//        String createSQL = "SELECT " + winnerOrLoser + "_id, tourney_id, surface, draw_size, tourney_level, " + wl + "_id, "
//                + wl + "_seed, " + wl + "_hand, " + wl + "_ht, "
//                + wl + "_ioc, " + wl + "_age, " + wl + "_rank, best_of, round, year, match_id\n" +
//                "INTO " + winnerOrLoser + "s\n" +
//                "FROM " + tableName + ";";
//
//        String addOutputColSQL = "ALTER TABLE " + winnerOrLoser + "s ADD COLUMN output INTEGER;";
//
//        String updateOutputSQL = "UPDATE " + winnerOrLoser + "s SET output = ? WHERE output ISNULL;";
//
//        try (Connection conn = ConnectDB.connect()) {
//
//            //create tables and add relevant data
//            PreparedStatement pstmtCreate = conn.prepareStatement(createSQL);
//            pstmtCreate.executeUpdate();
//
//            //add output column
//            PreparedStatement pstmtAdd = conn.prepareStatement(addOutputColSQL);
//            pstmtAdd.executeUpdate();
//
//            //if winner set output = 1, else if loser set output = 0
//            PreparedStatement pstmtUpdate = conn.prepareStatement(updateOutputSQL);
//            if (winnerOrLoser.equals("winner")) {
//                pstmtUpdate.setInt(1, 1);
//            } else
//                pstmtUpdate.setInt(1, 0);
//            pstmtUpdate.executeUpdate();
//
//        } catch (SQLException e) {
//            System.err.println("Error creating new table for " + winnerOrLoser + ":" + e.getMessage());
//        }
//    }

}
