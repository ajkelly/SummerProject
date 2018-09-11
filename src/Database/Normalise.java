package Database;

import Utils.DBUtils;

import java.sql.*;
import java.util.*;

/**
 * Class which contains the method to normalise the data from
 * individual rows of the tables. This will be performed using
 * min-max normalisation.
 *
 * @author Alex Kelly
 */
public class Normalise {

    private int scaledMax;
    private int scaledMin;

    /**
     * CONSTRUCTOR.
     *
     * Initialises the min and max to 0 and 1 respectively.
     */
    public Normalise() {
        this.scaledMax = 1;
        this.scaledMin = 0;
    }

    /**
     * Method to update a specified column to a float data
     * type.
     *
     * @param colName the column that needs it's type updating
     */
    public void updateColType(String colName) {

        String[] cols = DBUtils.updateColTypeCols();
        if (!(Arrays.asList(cols).contains(colName))) {
            throw new IllegalArgumentException("invalid col name '" + colName + "', options are " + Arrays.toString(cols));
        }

        String convertSQL = "ALTER TABLE training_data ALTER COLUMN " + colName +
                " TYPE float USING " + colName + "::float;";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement convert = conn.prepareStatement(convertSQL)) {

            convert.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating column " + colName + ": " + e);
            e.printStackTrace();
        }
    }

    /**
     * Method to check for null values in the winner/loser seed column
     * and update them with the value '40' prior to normalisation. The
     * regular seeds go up to 33 (number 1 seed being the favourite to
     * win the tournament and number 33 being the least favoured out of
     * all the seeds). The reason behind the number 40 is because leaving
     * the values as null will skew the normalisation, and initialising
     * the unseeded players with a random float may not accurately
     * demonstrate that they are less favoured than the seeded players.
     */
    public void unseeded() {

        String updateWinnerSQL = "UPDATE training_data SET winner_seed = 40 WHERE winner_seed ISNULL;";
        String updateLoserSQL = "UPDATE training_data SET loser_seed = 40 WHERE loser_seed ISNULL;";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmtWinner = conn.prepareStatement(updateWinnerSQL);
             PreparedStatement pstmtLoser = conn.prepareStatement(updateLoserSQL)) {

            pstmtWinner.executeUpdate();

            pstmtLoser.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating seeds: " + e);
        }
    }

    /**
     * Method for normalising the numeric data. It first finds the min and
     * max value from the particular column and stores them in an AL - min
     * at position 0 and max at position 1. It then selects all column values
     * and runs them through the min-max normalisation algorithm, and updates
     * the value within the db table for the normalised value.
     *
     * @param colName the column in the db table being normalised
     */
    public void normaliseValues(String colName) {

        if (!(colName.equals("draw_size") || colName.equals("best_of"))) {
            throw new IllegalArgumentException("invalid col name '" + colName + "'");
        }

        List<Float> values = new ArrayList<>();

        int min = 0;
        int max = 1;
        List<Float> minMaxValues = new ArrayList<>();

        String selectMaxSQL = "SELECT " + colName + " FROM training_data ORDER BY " + colName +
                " DESC LIMIT 1;";

        String selectMinSQL = "SELECT " + colName + " FROM training_data ORDER BY " + colName +
                " ASC LIMIT 1;";

        String selectAllSQL = "SELECT DISTINCT " + colName + " FROM training_data ORDER BY " + colName + " ASC;";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmtMin = conn.prepareStatement(selectMinSQL);
             ResultSet rsMin = pstmtMin.executeQuery()) {

            while (rsMin.next()) {
                minMaxValues.add(rsMin.getFloat(colName));
            }

            try (PreparedStatement pstmtMax = conn.prepareStatement(selectMaxSQL);
                 ResultSet rsMax = pstmtMax.executeQuery()) {

                while (rsMax.next()) {
                    minMaxValues.add(rsMax.getFloat(colName));
                }
            }

            System.out.println("min and max for " + colName + " = " + minMaxValues.toString());

            try (PreparedStatement pstmtAll = conn.prepareStatement(selectAllSQL);
            ResultSet rsAll = pstmtAll.executeQuery()) {

                while (rsAll.next()) {
                    values.add(rsAll.getFloat(colName));
                }
            }

            for (int i = 0; i < values.size(); i++) {
                Float scaledValue = ((values.get(i) - minMaxValues.get(min)) / (minMaxValues.get(max) - minMaxValues.get(min))) * (this.scaledMax - this.scaledMin) + this.scaledMin;
                String updateSQL = "UPDATE training_data SET " + colName + " = " + scaledValue + " WHERE " + colName + " = " + values.get(i) + ";";
                System.out.println(colName + " value: " + values.get(i).toString() + " = " + scaledValue.toString());
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateSQL)) {
                    pstmtUpdate.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.err.println("Error normalising " + colName + ": " + e);
            e.printStackTrace();
        }
    }

    /**
     * Method for normalising the numeric data whose heading has a winner and
     * loser column. It first finds the min and max value from the particular
     * column and stores them in an AL - min at position 0 and max at position
     * 1. It then selects all column values and runs them through the min-max
     * normalisation algorithm, and updates the value within the db table for
     * the normalised value.
     *
     * @param colName the column in the db table being normalised
     */
    public void normaliseWinnerLoser(String colName) {

        String[] cols = DBUtils.normaliseWinnerLoserCols();
        if (!(Arrays.asList(cols).contains(colName))) {
            throw new IllegalArgumentException("invalid col name '" + colName + "', options are " + Arrays.toString(cols));
        }

        String loserColName = "loser_" + colName;
        String winnerColName = "winner_" + colName;

        //0 represents index of min and 1 index of max
        int min = 0;
        int max = 1;
        List<Float> minMaxValues = new ArrayList<>();

        minMaxValues.add(Normalise.returnMin(colName));
        minMaxValues.add(Normalise.returnMax(colName));

        //order by used to show intuitive printout when running program
        String selectAllLoserSQL = "SELECT DISTINCT " + loserColName + " FROM training_data ORDER BY " + loserColName + " ASC;";
        String selectAllWinnerSQL = "SELECT DISTINCT " + winnerColName + " FROM training_data ORDER BY " + winnerColName + " ASC;";

        System.out.println("min and max for " + colName + " = " + minMaxValues.toString());

        Set<Float> set = new LinkedHashSet<>(); //to add values to avoid duplicates

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmtL = conn.prepareStatement(selectAllLoserSQL);
             ResultSet rsl = pstmtL.executeQuery();
             PreparedStatement pstmtW = conn.prepareStatement(selectAllWinnerSQL);
             ResultSet rsw = pstmtW.executeQuery()) {

            while (rsl.next()) {
                set.add(rsl.getFloat(loserColName));
            }
            while (rsw.next()) {
                set.add(rsw.getFloat(winnerColName));
            }

            List<Float> values = new ArrayList<>(set); // convert to access by index

            for (int i = 0; i < values.size(); i++) {
                Float scaledValue = ((values.get(i) - minMaxValues.get(min)) / (minMaxValues.get(max) - minMaxValues.get(min))) * (this.scaledMax - this.scaledMin) + this.scaledMin;

                String updateWinnerSQL = "UPDATE training_data SET " + winnerColName + " = " + scaledValue + " WHERE " + winnerColName + " = " + values.get(i) + ";";
                String updateLoserSQL = "UPDATE training_data SET " + loserColName + " = " + scaledValue + " WHERE " + loserColName + " = " + values.get(i) + ";";

                System.out.println(colName + " value: " + values.get(i).toString() + " = " + scaledValue.toString());

                try (PreparedStatement pstmtUpdateW = conn.prepareStatement(updateWinnerSQL);
                     PreparedStatement pstmtUpdateL = conn.prepareStatement(updateLoserSQL)) {

                    pstmtUpdateW.executeUpdate();
                    pstmtUpdateL.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method to return the highest value out of the
     * highest value from the winner column and the highest
     * value from the loser column, for a particular column.
     *
     * @param colName the column being checked
     * @return the higher value
     */
    private static float returnMax(String colName) {

        String loserColName = "loser_"+colName;
        String winnerColName = "winner_"+colName;

        String selectMaxLoserSQL = "SELECT " + loserColName + " FROM training_data ORDER BY " + loserColName +
                " DESC LIMIT 1;";
        String selectMaxWinnerSQL = "SELECT " + winnerColName + " FROM training_data ORDER BY " + winnerColName +
                " DESC LIMIT 1;";

        List<Float> cmp = new ArrayList<>();

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmtLoser = conn.prepareStatement(selectMaxLoserSQL);
             ResultSet rsl = pstmtLoser.executeQuery();
             PreparedStatement pstmtWinner = conn.prepareStatement(selectMaxWinnerSQL);
             ResultSet rsw = pstmtWinner.executeQuery()) {

            while (rsl.next()) {
                cmp.add(rsl.getFloat(loserColName));
            }
            while (rsw.next()) {
                cmp.add(rsw.getFloat(winnerColName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Collections.max(cmp);
    }

    /**
     * Helper method to return the highest value out of the
     * highest value from the winner column and the highest
     * value from the loser column, for a particular column.
     *
     * @param colName the column being checked
     * @return the higher value
     */
    private static float returnMin(String colName) {

        String loserColName = "loser_"+colName;
        String winnerColName = "winner_"+colName;

        String selectMaxLoserSQL = "SELECT " + loserColName + " FROM training_data ORDER BY " + loserColName +
                " ASC LIMIT 1;";
        String selectMaxWinnerSQL = "SELECT " + winnerColName + " FROM training_data ORDER BY " + winnerColName +
                " ASC LIMIT 1;";

        List<Float> cmp = new ArrayList<>();

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmtLoser = conn.prepareStatement(selectMaxLoserSQL);
             ResultSet rsl = pstmtLoser.executeQuery();
             PreparedStatement pstmtWinner = conn.prepareStatement(selectMaxWinnerSQL);
             ResultSet rsw = pstmtWinner.executeQuery()) {

            while (rsl.next()) {
                cmp.add(rsl.getFloat(loserColName));
            }
            while (rsw.next()) {
                cmp.add(rsw.getFloat(winnerColName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Collections.min(cmp);
    }

}
