package Database;

import Utils.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Contains methods which remove unneeded data from the training
 * data table, as well as several helper methods. Also contains
 * method to add pkey (match_id) and output columns to training_data.
 *
 * @author Alex Kelly
 */
public class CleanDatabase {

    /**
     * Method which removes the columns in the specified table that aren't needed.
     * Columns are listed in deleteSQL variable.
     */
    public void deleteUnneededColumns() {

        String deleteSQL = "ALTER TABLE training_data DROP COLUMN tourney_id, DROP COLUMN tourney_date, DROP COLUMN match_num,\n" +
                "                DROP COLUMN winner_rank_points, DROP COLUMN winner_name, DROP COLUMN loser_name, DROP COLUMN loser_rank_points,\n" +
                "                DROP COLUMN score, DROP COLUMN minutes_, DROP COLUMN w_bpfaced, DROP COLUMN w_svpt, DROP COLUMN w_1stin,\n" +
                "                DROP COLUMN w_1stwon, DROP COLUMN w_ace, DROP COLUMN w_bpsaved, DROP COLUMN w_2ndwon, DROP COLUMN w_df,\n" +
                "                DROP COLUMN w_svgms, DROP COLUMN l_bpfaced, DROP COLUMN l_svpt, DROP COLUMN l_1stin, DROP COLUMN l_1stwon,\n" +
                "                DROP COLUMN l_ace, DROP COLUMN l_bpsaved, DROP COLUMN l_2ndwon, DROP COLUMN l_df, DROP COLUMN l_svgms,\n" +
                "                DROP COLUMN winner_entry, DROP COLUMN loser_entry, DROP COLUMN winner_age, DROP COLUMN loser_age;";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {

            pstmt.executeUpdate();
            System.out.println("27 unneeded columns removed from training_data");

        } catch (SQLException e) {
            System.err.println("Error deleting unneeded columns: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Method to delete blank String or null values from the winner_rank,
     * and loser_rank columns in training_data table.
     */
    private void convertRankToIntHelper() {

        String deleteNull = "DELETE FROM training_data WHERE winner_rank ='' OR loser_rank ='';";
        String deleteString = "DELETE FROM training_data" +
                " WHERE winner_rank = 'winner_rank'  OR loser_rank = 'loser_rank';";

        //delete nulls
        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmtNull = conn.prepareStatement(deleteNull)) {

            pstmtNull.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting nulls: " + e.getMessage());
            e.printStackTrace();
        }

        //delete Strings
        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmtString = conn.prepareStatement(deleteString)) {

            pstmtString.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting Strings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Method to convert varchar winner/loser_rank columns into integer
     * so that the next method -> removeLowerRankedPlayers() -> can perform
     * its task.
     */
    private void convertRankToInt() {

        //removes blanks and nulls
        convertRankToIntHelper();

        String winRankToIntSQL = "ALTER TABLE training_data ALTER COLUMN winner_rank TYPE INT USING winner_rank::integer;";
        String loseRankToIntSQL = "ALTER TABLE training_data ALTER COLUMN loser_rank TYPE INT USING loser_rank::integer;";

        //convert the winner_entry column to an INT
        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmtWinner = conn.prepareStatement(winRankToIntSQL)) {

            pstmtWinner.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error converting winner_rank: " + e);
            e.printStackTrace();
        }

        //convert the loser_entry column to an INT
        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmtLoser = conn.prepareStatement(loseRankToIntSQL)) {

            pstmtLoser.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error converting loser_rank: " + e);
            e.printStackTrace();
        }
    }

    /**
     * Method for removing the players in the database that are ranked
     * higher than 50.
     */
    public void removeLowerRankedPlayers() {

        convertRankToInt();

        String top50SQL = "DELETE FROM training_data WHERE winner_rank > 50 OR loser_rank > 50;";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmtLoser = conn.prepareStatement(top50SQL)) {

            int rows = pstmtLoser.executeUpdate();
            System.out.println("lower ranked players successfully removed from training_data (" + rows + " rows)");

        } catch (SQLException e) {
            System.err.println("Error removing players ranked below 50: " + e);
            e.printStackTrace();
        }
    }

    /**
     * Method to delete empty String values from columns.
     */
    public void removeBlankValues() {

        String[] colNames = DBUtils.removeBlankValCols();

        for (int x = 0; x < colNames.length; x++) {
            String deleteNull = "DELETE FROM training_data WHERE " + colNames[x] + " ='';";
            try (Connection conn = ConnectDB.connect();
                 PreparedStatement del = conn.prepareStatement(deleteNull)) {

                del.executeUpdate();

            } catch (SQLException e) {
                System.out.println("error deleting blanks in column " + colNames[x]);
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to convert blank string values to nulls in the winner and
     * loser_seed columns.
     *
     * @param colName the specific column being modified
     */
    public void convertBlankToNull(String colName) {

        if (!(colName.equals("winner_seed") || colName.equals("loser_seed"))) {
            throw new IllegalArgumentException("invalid col name");
        }

        String convertBlanks = "UPDATE training_data SET " + colName + " = NULL WHERE " + colName + " = '';";
        try (Connection conn = ConnectDB.connect();
             PreparedStatement conv = conn.prepareStatement(convertBlanks)) {

            conv.executeUpdate();

        } catch (SQLException e) {
            System.out.println("error converting blanks to nulls");
            e.printStackTrace();
        }
    }

    /**
     * Method that adds an auto incrementing integer primary-key
     * match_id column to each entry in the table.
     */
    public void addUniqueMatchID() {

        String addPKey = "ALTER TABLE training_data ADD COLUMN match_id SERIAL PRIMARY KEY;";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(addPKey)) {

            pstmt.executeUpdate();
            System.out.println("primary key 'match_id' added to training_data");

            System.out.println(DBUtils.countRows("training_data") + " rows remaining in training_data");

        } catch (SQLException e) {
            System.err.println("Error adding pkey: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Method for adding two output columns which will allow for
     * supervised learning to take place. The first column is
     * better_rank_won which will contain the value 1 if the
     * player with the better rank won, and the second column is
     * worse_rank_won which will contain the value 1 if the lower
     * ranked player won. In each case the other column will contain
     * a 0.
     *
     * Method creates the output columns and then takes in each entry
     * within the training_data table and compares the winner and loser
     * rank.
     */
    public void createOutputColumns() {

        String rowCountSQL = "SELECT count(*) AS rows FROM training_data;";

        int rowCount = 0;

        try (Connection conn1 = ConnectDB.connect();
            PreparedStatement psCountRows = conn1.prepareStatement(rowCountSQL);
            ResultSet rsCountRows = psCountRows.executeQuery()) {

            rsCountRows.next();
            rowCount = rsCountRows.getInt("rows");
            System.out.println("row count = " + rowCount);
        } catch (SQLException e) {
            System.err.println(e);
            e.printStackTrace();
        }

        int counter = 1;
        int wRank;
        int lRank;
        //add the two new output columns
        String addColHigherSQL = "ALTER TABLE training_data ADD COLUMN better_rank_won INTEGER;";
        String addColLowerSQL = "ALTER TABLE training_data ADD COLUMN worse_rank_won INTEGER;";

        try (Connection conn2 = ConnectDB.connect();
             PreparedStatement psaddCol1 = conn2.prepareStatement(addColHigherSQL);
             PreparedStatement psaddCol2 = conn2.prepareStatement(addColLowerSQL)) {

            psaddCol1.executeUpdate();

            psaddCol2.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e);
            e.printStackTrace();
        }

        while (counter <= rowCount) {

            String selectWRSQL = "SELECT winner_rank FROM training_data WHERE match_id = " + counter + ";";
            String selectLRSQL = "SELECT loser_rank FROM training_data WHERE match_id = " + counter + ";";

            try (Connection conn3 = ConnectDB.connect();
                 PreparedStatement pstmtWR = conn3.prepareStatement(selectWRSQL);
                 ResultSet rsWR = pstmtWR.executeQuery();
                 PreparedStatement pstmtLR = conn3.prepareStatement(selectLRSQL);
                 ResultSet rsLR = pstmtLR.executeQuery()) {

                //store winner rank
                rsWR.next();
                wRank = rsWR.getInt("winner_rank");

                //store loser rank
                rsLR.next();
                lRank = rsLR.getInt("loser_rank");

//                System.out.println("row " + counter + ". winner rank -> " + wRank + " --- loser rank -> " + lRank);

                //if winner rank is less than loser rank it means the better ranked player won
                if (wRank < lRank) {
                    String updateColHigherSQL = "UPDATE training_data SET better_rank_won = ? WHERE match_id = " + counter + ";";
                    String updateColLowerSQL = "UPDATE training_data SET worse_rank_won = ? WHERE match_id = " + counter + ";";

                    try (PreparedStatement pstmt1 = conn3.prepareStatement(updateColHigherSQL);
                         PreparedStatement pstmt2 = conn3.prepareStatement(updateColLowerSQL)) {

                        pstmt1.setInt(1, 1);
                        pstmt1.executeUpdate();

                        pstmt2.setInt(1, 0);
                        pstmt2.executeUpdate();
                        if (counter % 100 == 0) {
                            System.out.println("row " + counter + ". better ranked player " + wRank + ", beat worse ranked player " + lRank);
                        }
                    }
                    //otherwise the worse ranked player won
                } else {
                    String updateColHigherSQL = "UPDATE training_data SET better_rank_won = ? WHERE match_id = " + counter + ";";
                    String updateColLowerSQL = "UPDATE training_data SET worse_rank_won = ? WHERE match_id = " + counter + ";";

                    try (PreparedStatement pstmt1 = conn3.prepareStatement(updateColHigherSQL);
                         PreparedStatement pstmt2 = conn3.prepareStatement(updateColLowerSQL)) {

                        pstmt1.setInt(1, 0);
                        pstmt1.executeUpdate();

                        pstmt2.setInt(1, 1);
                        pstmt2.executeUpdate();
                        if (counter % 100 == 0) {
                            System.out.println("row " + counter + ". worse ranked player " + wRank + ", beat better ranked player " + lRank);
                        }
                    }
                }

                counter++;

            } catch (SQLException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

//    //code elsewhere changed since tested, may no longer be compatible
//    /**
//     * Method that checks whether the loser/winner_hand column is empty or contains
//     * anything other than 'L' or 'R'. If the column is empty, it takes the
//     * loser/winner_id and checks if that player has any other entries within the table
//     * where the hand is filled in. If so, it fills in the blank value and moves
//     * on, if not, it deletes the row.
//     *
//     * @param tableName the table in the database being altered
//     * @param loserOrWinner specify whether loser or winner
//     */
//    private void updatePlayerHand(String tableName, String loserOrWinner) {
//
//        if (!(loserOrWinner.equals("winner") || loserOrWinner.equals("loser"))) {
//            throw new IllegalArgumentException();
//        }
//
//        if (!(tableName.equals("training_data") || tableName.equals("test_data"))) {
//            throw new IllegalArgumentException();
//        }
//
//        //to store the player_id of the blank values
//        ArrayList<Integer> lOrWHand = new ArrayList<>();
//        //find the rows with null or incorrect value
//        String selectHandSQL = "SELECT " + loserOrWinner + "_hand, " + loserOrWinner + "_id FROM " + tableName +
//                " WHERE " + loserOrWinner + "_hand ='' OR " + loserOrWinner + "_hand ='U';";
//
//        try (Connection conn = ConnectDB.connect();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(selectHandSQL)) {
//
//            while (rs.next()) {
//                /* add the winner_id to the list so it can be used to search for
//                   other entries from that player */
//                lOrWHand.add(rs.getInt(loserOrWinner + "_id"));
//            }
//
//            for (int i = 0; i < lOrWHand.size(); i++) {
//                //iterate over winner_id's and check for more entries with that id
//                int id = lOrWHand.get(i);
//                String checkIDSQL = "SELECT " + loserOrWinner + "_hand, " + loserOrWinner + "_id FROM " + tableName +
//                        " WHERE " + loserOrWinner + "_id = " + id + ";";
//
//                try (Statement stmt1 = conn.createStatement();
//                     ResultSet rs1 = stmt1.executeQuery(checkIDSQL)) {
//
//                    String hand = rs1.getString(loserOrWinner + "_hand");
//                    if (hand.equals("L") || hand.equals("R")) {
//                        String updateHandSQL = "UPDATE " + tableName + " SET " + loserOrWinner + "_hand = '" + hand +
//                                "' WHERE " + loserOrWinner + "_id = " + id + " " + loserOrWinner + "_hand ='' OR "
//                                + loserOrWinner + "_hand ='U';";
//
//                        try (Statement stmt2 = conn.createStatement()) {
//                            stmt2.executeUpdate(updateHandSQL);
//
//                        } catch (SQLException e) {
//                            System.err.println("Error with winner_id: " + e.getMessage());
//                            e.printStackTrace();
//                        }
//
//                    } else {
//                        //delete the row if there are not other rows with the hand information
//                        String deleteRowSQL = "DELETE FROM " + tableName + " WHERE id = " + id + ";";
//
//                        try (Statement stmt3 = conn.createStatement()) {
//                            stmt3.executeUpdate(deleteRowSQL);
//
//                        } catch (SQLException e) {
//                            System.err.println("Error deleting row: " + e.getMessage());
//                            e.printStackTrace();
//                        }
//                    }
//
//                } catch (SQLException e) {
//                    System.err.println("Error with winner_id: " + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//
//        } catch (SQLException e) {
//            System.err.println("Error updating winner hand info: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Method that checks whether the loser/winner_ht column is empty. If it is, it takes
//     * the loser/winner_id and checks if they have any other entries within the table
//     * where the height is filled in. If so, it fills in the blank value and moves
//     * on, if not, it deletes the row.
//     *
//     * @param tableName the table in the database being altered
//     * @param loserOrWinner specify loser or winner height
//     */
//    private void updatePlayerHeight(String tableName, String loserOrWinner) {
//
//        if (!(loserOrWinner.equals("winner") || loserOrWinner.equals("loser"))) {
//            throw new IllegalArgumentException();
//        }
//
//        if (!(tableName.equals("training_data") || tableName.equals("test_data"))) {
//            throw new IllegalArgumentException();
//        }
//
//        ArrayList<Integer> wHeight = new ArrayList<>();
//
//        String selectHtSQL = "SELECT " + loserOrWinner + "_ht, " + loserOrWinner + "_id FROM " + tableName +
//                " WHERE " + loserOrWinner + "_ht ISNULL;";
//
//        try (Connection conn = ConnectDB.connect();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(selectHtSQL)) {
//
//            while (rs.next()) {
//                wHeight.add(rs.getInt(loserOrWinner + "_id"));
//            }
//
//            for (int i = 0; i < wHeight.size(); i++) {
//                int id = wHeight.get(i);
//                String checkIDSQL = "SELECT " + loserOrWinner + "_ht, " + loserOrWinner + "_id FROM " + tableName +
//                        " WHERE " + loserOrWinner + "_id = " + id + ";";
//
//                try (Statement stmt1 = conn.createStatement();
//                     ResultSet rs1 = stmt1.executeQuery(checkIDSQL)) {
//
//                    Integer ht = rs1.getInt(loserOrWinner + "_ht");
//                    //check if null by converting to a String
//                    if (!ht.toString().equals("")) {
//                        String updateHandSQL = "UPDATE " + tableName + " SET " + loserOrWinner + "_ht = " + ht +
//                                " WHERE " + loserOrWinner + "_id = " + id + " AND " + loserOrWinner + "_ht ISNULL;";
//
//                        try (Statement stmt2 = conn.createStatement()) {
//                            stmt2.executeUpdate(updateHandSQL);
//
//                        } catch (SQLException e) {
//                            System.err.println("Error updating height: " + e.getMessage());
//                            e.printStackTrace();
//                        }
//
//                    } else {
//                        //delete the row if there are not other rows with the height information
//                        String deleteRowSQL = "DELETE FROM " + tableName + " WHERE " + loserOrWinner + "_id = " + id +
//                                " AND " + loserOrWinner + "_ht ISNULL;";
//
//                        try (Statement stmt3 = conn.createStatement()) {
//                            stmt3.executeUpdate(deleteRowSQL);
//
//                        } catch (SQLException e) {
//                            System.err.println("Error deleting row: " + e.getMessage());
//                            e.printStackTrace();
//                        }
//                    }
//
//                } catch (SQLException e) {
//                    System.err.println("Error checking for other entries with id: " + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//
//        } catch (SQLException e) {
//            System.err.println("Error checking for null height: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

}

