package Utils;

import Database.ConnectDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * Class which contains the utility methods to help
 * with the Database component.
 *
 * @author Alex Kelly
 */
public class DBUtils {

    /**
     * Method to return the number of rows in the db table.
     *
     * @param tableName the table to count the rows of
     * @return an integer value for the number of rows
     */
    public static int countRows(String tableName) {

        if (!(tableName.equals("training_data") || tableName.equals("test_data"))) {
            throw new IllegalArgumentException();
        }

        String rowCountSQL = "SELECT count(*) AS rows FROM " + tableName + ";";

        int rowCount = 0;

        try (Connection conn1 = ConnectDB.connect()){
            PreparedStatement psCountRows = conn1.prepareStatement(rowCountSQL);
            ResultSet rsCountRows = psCountRows.executeQuery();
            rsCountRows.next();
            rowCount = rsCountRows.getInt("rows");
//            System.out.println("row count = " + rowCount);

        } catch (SQLException e) {
            System.err.println(e);
            e.printStackTrace();
        }

        return rowCount;
    }

    /**
     * Method to obtain the column headings from the specified
     * database table, used to check arguments passed.
     *
     * @return array of column headings
     */
    public static String[] getColNames(String tableName) {

        if (!(tableName.equals("training_data") || tableName.equals("test_data"))){
            throw new IllegalArgumentException("invalid table name: " + tableName);
        }

        String[] colNames = null;

        String selectSQL = "SELECT * FROM " + tableName + ";";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL);
             ResultSet rs = pstmt.executeQuery()) {

            ResultSetMetaData rsmd = rs.getMetaData();

            int colCount = rsmd.getColumnCount();

            colNames = new String[colCount];

            //column count starts from 1
            for (int i = 1; i <= colCount; i++) {
                String name = rsmd.getColumnName(i);
                colNames[i-1] = name;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
//        System.out.println(Arrays.toString(colNames)); //test
        return colNames;
    }

    /**
     * Method to provide column headings to Normalise class method
     * normaliseValues.
     *
     * @return String array of the column headings
     */
    public static String[] updateColTypeCols() {

        return new String[]{"loser_ht", "winner_ht", "draw_size", "winner_rank",
                "loser_rank", "winner_seed", "loser_seed", "best_of"};
    }

    /**
     * Method to provide column headings to Normalise class method
     * normaliseValues.
     *
     * @return String array of the column headings
     */
    public static String[] normaliseWinnerLoserCols() {

        return new String[]{"rank", "ht", "seed"};
    }

    /**
     * Method to provide column headings to CleanDatabase class method
     * removeBlankValues.
     *
     * @return String array of the column headings
     */
    public static String[] removeBlankValCols() {

        return new String[]{"tourney_name", "surface", "draw_size", "tourney_level", "winner_id", "winner_hand", "winner_ht",
                 "winner_ioc", "loser_id", "loser_hand", "loser_ht", "loser_ioc", "best_of", "round", "year"};
//                "winner_rank", "loser_rank"};
    }

    /**
     * Method to provide column headings to StandardiseNonNumeric
     * class method standardiseIndividualCol.
     *
     * @return String array of the column headings
     */
    public static String[] standardiseGenColCols() {

        return new String[]{"tourney_level", "surface", "tourney_name", "round"};
    }

    /**
     * Method to provide the available years that can be added to the
     * test_data table from the training_data table.
     *
     * @return int array of years in training_data
     */
    public static int[] createTestDataYears() {

        return new int[]{2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2018};
    }

    /**
     * Method to print ArrayList of elements.
     *
     * @param list the ArrayList to be printed
     */
    public static void printArrayList(ArrayList<?> list) {
        if (list.size() < 1) {
            throw new IllegalArgumentException("list must contain elements");
        }

        for (Object e : list) {
            System.out.print(e + ", ");
        }
    }

    /**
     * Method to print 2 ArrayLists of elements side by side.
     *
     * @param list1 the first ArrayList to be printed
     * @param list2 the second ArrayList to be printed
     */
    public static void printArrayList(ArrayList<?> list1, ArrayList<?> list2) {
        if (list1.size() < 1 || list2.size() < 1) {
            throw new IllegalArgumentException("list must contain elements");
        }

        int count = 1;

        for (Object i : list1) {
            for (Object j : list2) {
                System.out.println(count + ". " + i + " -> " + j);
                count++;
            }
        }
    }

    /**
     * Generic method to return the index of a set where a
     * particular element is located.
     *
     * @param set the set containing the element
     * @param element the value being looked up
     * @return the index at which the element can be found
     */
    public static int getSetIndex(Set<? extends Object> set, Object element) {
        int index = 0;

        for (Object e: set) {
            if (e.equals(element)) return index;

            index++;
        }
        throw new IllegalArgumentException("element '" + element + "' not present");
    }


}
