package Network;

import Database.ConnectDB;
import Utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Class which contains the methods required to create
 * a set of data from the data found in the database,
 * which will either be used to train or test the network,
 * depending on which db table it is read in from.
 * A set consists of 2D float arrays - input and output -
 * stored at index 0 and 1 of an ArrayList, so that
 * the entire data set can be trained as a whole.
 *
 * @author Alex Kelly
 */
public class CreateNetworkData {

    /**
     * Method which reads the training/test data input into a 2D
     * array of type float and returns it.
     *
     * @param tableName the table being trained
     * @return training/test data input
     */
    private static float[][] createDataInput(String tableName) {

        if (!(tableName.equals("training_data") || tableName.equals("test_data"))) {
            throw new IllegalArgumentException("invalid table name: " + tableName);
        }

        int cols = 16;
        int rowCount = DBUtils.countRows(tableName);

        float[][] trainingInput = new float[rowCount][cols];

        String selectSQL = "SELECT tourney_name, surface, draw_size, tourney_level, winner_seed, winner_hand, winner_ht, " +
                "winner_ioc, winner_rank, loser_seed, loser_hand, loser_ht, loser_ioc, loser_rank, best_of, round FROM " +
                tableName + " ORDER BY match_id ASC;";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL);
             ResultSet rs = pstmt.executeQuery()) {

            int row = 0;
            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    trainingInput[row][i-1] = rs.getFloat(i);
//                    System.out.println("row: " + row + " --> col: " + i);
                    if (i % cols == 0) {
                        row++;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
//        System.out.println("input: " + Arrays.deepToString(trainingInput) + "\n");
        return trainingInput;
    }

    /**
     * Method which reads the training/test data output into an array of type float
     * and returns it.
     *
     * @param tableName the table being trained
     * @return training/test data output
     */
    private static float[][] createDataOutput(String tableName) {

        if (!(tableName.equals("training_data") || tableName.equals("test_data"))) {
            throw new IllegalArgumentException("invalid table name: " + tableName);
        }

        int cols = 2;
        int rowCount = DBUtils.countRows(tableName);

        float[][] trainingOutput = new float[rowCount][cols];

        String selectSQL = "SELECT better_rank_won, worse_rank_won FROM " + tableName + " ORDER BY match_id ASC;";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL);
             ResultSet rs = pstmt.executeQuery()) {

            int row = 0;
            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    trainingOutput[row][i-1] = rs.getFloat(i);
//                    System.out.println("row: " + row + " --> col: " + i);
                    if (i % cols == 0) {
                        row++;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
//        System.out.println("output: " + Arrays.deepToString(trainingOutput));
        return trainingOutput;
    }

    /**
     * Method for returning the input and output data as a NetworkData.
     *
     * @param set the set of input and output
     * @param tableName table from the db to create the set from
     * @return a NetworkData of input and expected output
     */
    public NetworkData createNetworkData(NetworkData set, String tableName) {
        //check for valid table name
        if (!(tableName.equals("training_data") || tableName.equals("test_data"))) {
            throw new IllegalArgumentException("invalid table name: " + tableName);
        }

        float[][] input = createDataInput(tableName);
        float[][] output = createDataOutput(tableName);
        //check the input and output lengths correspond
        if (input.length != output.length) {
            throw new IllegalArgumentException("input length: " + input.length +
                    " must be the same as the output length: " + output.length);
        }

//        NeuralNetworkUtils.print2DArray(input, "input", output, "output");

        for (int row = 0; row < input.length; row++) {
            set.addNetworkData(input[row], output[row]);
        }

        return set;
    }


}
