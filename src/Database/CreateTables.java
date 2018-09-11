package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Contains the methods for creating the database tables.
 *
 * @author Alex Kelly
 */
public class CreateTables {

    /**
     * Creates the training_data table which will read in all the CSV files
     * initially (50 columns of data).
     */
    public void createTrainingData() {

        String createTableSQL = "CREATE TABLE training_data (\n" +
                "tourney_id VARCHAR(40),\n" +
                "tourney_name VARCHAR(30),\n" +
                "surface VARCHAR(30),\n" +
                "draw_size VARCHAR(30),\n" +
                "tourney_level VARCHAR(30),\n" +
                "tourney_date CHAR(30),\n" +
                "match_num VARCHAR(30),\n" +
                "winner_id VARCHAR(30) NOT NULL,\n" +
                "winner_seed VARCHAR(30),\n" +
                "winner_entry VARCHAR(30),\n" +
                "winner_name VARCHAR(40),\n" +
                "winner_hand CHAR(30),\n" +
                "winner_ht VARCHAR(30),\n" +
                "winner_ioc CHAR(30),\n" +
                "winner_age VARCHAR(30),\n" +
                "winner_rank VARCHAR(30),\n" +
                "winner_rank_points VARCHAR(30),\n" +
                "loser_id VARCHAR(30) NOT NULL,\n" +
                "loser_seed VARCHAR(30),\n" +
                "loser_entry VARCHAR(30),\n" +
                "loser_name VARCHAR(40),\n" +
                "loser_hand CHAR(30),\n" +
                "loser_ht VARCHAR(30),\n" +
                "loser_ioc CHAR(30),\n" +
                "loser_age VARCHAR(30),\n" +
                "loser_rank VARCHAR(30),\n" +
                "loser_rank_points VARCHAR(30),\n" +
                "score VARCHAR(40),\n" +
                "best_of VARCHAR(30),\n" +
                "round VARCHAR(30),\n" +
                "minutes_ VARCHAR(30),\n" +
                "w_ace VARCHAR(30),\n" +
                "w_df VARCHAR(30),\n" +
                "w_svpt VARCHAR(30),\n" +
                "w_1stIn VARCHAR(30),\n" +
                "w_1stWon VARCHAR(30),\n" +
                "w_2ndWon VARCHAR(30),\n" +
                "w_SvGms VARCHAR(30),\n" +
                "w_bpSaved VARCHAR(30),\n" +
                "w_bpFaced VARCHAR(30),\n" +
                "l_ace VARCHAR(30),\n" +
                "l_df VARCHAR(30),\n" +
                "l_svpt VARCHAR(30),\n" +
                "l_1stIn VARCHAR(30),\n" +
                "l_1stWon VARCHAR(30),\n" +
                "l_2ndWon VARCHAR(30),\n" +
                "l_SvGms VARCHAR(30),\n" +
                "l_bpSaved VARCHAR(30),\n" +
                "l_bpFaced VARCHAR(30),\n" +
                "year VARCHAR(30)\n" +
                ");";

        //use auto closable for resources throughout
        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(createTableSQL)) {

            pstmt.executeUpdate();
            System.out.println("training_data table created");

        } catch (SQLException e) {
            System.err.println("Error creating training_data: " + e.getMessage());
            e.printStackTrace();
        }
    }

//    /**
//     * Creates the test_data table which is only created with 21 columns
//     * because when the data is added to this the unneeded columns will
//     * have already been removed.
//     */
//    public void createTestData() {
//
//        String createTableSQL = "CREATE TABLE test_data (\n" +
//                "tourney_id VARCHAR(40),\n" +
//                "surface VARCHAR(30),\n" +
//                "draw_size VARCHAR(30),\n" +
//                "tourney_level VARCHAR(30),\n" +
//                "winner_id VARCHAR(30) NOT NULL,\n" +
//                "winner_seed VARCHAR(30),\n" +
//                "winner_hand CHAR(30),\n" +
//                "winner_ht VARCHAR(30),\n" +
//                "winner_ioc CHAR(30),\n" +
//                "winner_rank VARCHAR(30),\n" +
//                "loser_id VARCHAR(30) NOT NULL,\n" +
//                "loser_seed VARCHAR(30),\n" +
//                "loser_hand CHAR(30),\n" +
//                "loser_ht VARCHAR(30),\n" +
//                "loser_ioc CHAR(30),\n" +
//                "loser_rank VARCHAR(30),\n" +
//                "best_of VARCHAR(30),\n" +
//                "round VARCHAR(30),\n" +
//                "year VARCHAR(30),\n" +
//                "match_id INT,\n" +
//                "better_rank_won INT,\n" +
//                "worse_rank_won INT\n" +
//                ");";
//
//        try (Connection conn = ConnectDB.connect();
//             PreparedStatement pstmt = conn.prepareStatement(createTableSQL)) {
//
//            pstmt.executeUpdate();
//            System.out.println("test_data table created");
//
//        } catch (SQLException e) {
//            System.err.println("Error creating test_data: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
}
