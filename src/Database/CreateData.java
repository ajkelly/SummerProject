package Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Contains methods to create data needed to train network.
 *
 * @author Alex Kelly
 */
public class CreateData implements ICreateData {

    /**
     * Method which takes the winners (first serves won / first serves in) * 100 to
     * give a percentage of the winners' first serves won.
     *
     * @return wFirst, an ArrayList of the winners first serves won in the form
     * of a percentage
     */
    @Override
    public ArrayList<Float> wPercentageFirstServesWon(String tableName) {

        ArrayList<Float> wFirst = new ArrayList<>();

        String wfirstSQL = "SELECT CAST(w_1stwon AS FLOAT) / CAST(w_1stin AS FLOAT) * 100 " +
                "AS wFirstServePercentage FROM " + tableName + ";";

        try (Connection conn = ConnectDB.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(wfirstSQL)) {

            while (rs.next()) {
                wFirst.add(rs.getFloat("wFirstServePercentage"));
            }

        } catch (SQLException e) {
            System.err.println("error accessing winner 1st serve percentage: " + e);
        }

        return wFirst;
    }

    /**
     * Method which takes the winners (second serves won / second serves in) * 100 to
     * give a percentage of the winners' second serves won.
     *
     * @return wSecond, an ArrayList of the winners second serves won in the form
     *  of a percentage
     */
    @Override
    public ArrayList<Float> wPercentageSecondServesWon(String tableName) {

        ArrayList<Float> wSecond = new ArrayList<>();

        String wSecondSQL = "SELECT CAST(w_2ndwon AS FLOAT) / (CAST(w_svpt AS FLOAT) - " +
                "CAST (w_1stin AS FLOAT)) * 100 AS wSecondServePercentage FROM " + tableName + ";";

        try (Connection conn = ConnectDB.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(wSecondSQL)) {

            while (rs.next()) {
                wSecond.add(rs.getFloat("wSecondServePercentage"));
            }

        } catch (SQLException e) {
            System.err.println("error accessing winner 2nd serve percentage: " + e);
        }

        return wSecond;
    }

    /**
     * Method that takes the winners break points faced - break points saved
     * to work out the amount of service breaks against them.
     *
     * @return wBroken, an ArrayList of the amount of service breaks
     */
    @Override
    public ArrayList<Integer> wServiceBreaksAgainst(String tableName) {

        ArrayList<Integer> wBroken = new ArrayList<>();

        String wBrokenSQL = "SELECT w_bpfaced - w_bpsaved AS wBreaks FROM " + tableName + ";";

        try (Connection conn = ConnectDB.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(wBrokenSQL)) {

            while (rs.next()) {
                wBroken.add(rs.getInt("wBreaks"));
            }

        } catch (SQLException e) {
            System.err.println("error accessing winner break points lost: " + e);
        }

        return wBroken;
    }

    /**
     * Method which takes the losers (first serves won / first serves in) * 100 to
     * give a percentage of the losers' first serves won.
     *
     * @return lFirst, an ArrayList of the losers first serves won in the form
     * of a percentage
     */
    @Override
    public ArrayList<Float> lPercentageFirstServesWon(String tableName) {

        ArrayList<Float> lFirst = new ArrayList<>();

        String lfirstSQL = "SELECT CAST(l_1stwon AS FLOAT) / CAST(l_1stin AS FLOAT) * 100 " +
                "AS lFirstServePercentage FROM " + tableName + ";";

        try (Connection conn = ConnectDB.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(lfirstSQL)) {

            while (rs.next()) {
                lFirst.add(rs.getFloat("lFirstServePercentage"));
            }

        } catch (SQLException e) {
            System.err.println("error accessing loser 1st serve percentage: " + e);
        }

        return lFirst;
    }

    /**
     * Method which takes the winners (second serves won / second serves in) * 100 to
     * give a percentage of the winners' second serves won.
     *
     * @return lSecond, an ArrayList of the winners second serves won in
     * the form of a percentage
     */
    @Override
    public ArrayList<Float> lPercentageSecondServesWon(String tableName) {

        ArrayList<Float> lSecond = new ArrayList<>();

        String lSecondSQL = "SELECT CAST(l_2ndwon AS FLOAT) / (CAST(l_svpt AS FLOAT) - " +
                "CAST (l_1stin AS FLOAT)) * 100 AS lSecondServePercentage FROM " + tableName + ";";

        try (Connection conn = ConnectDB.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(lSecondSQL)) {

            while (rs.next()) {
                lSecond.add(rs.getFloat("lSecondServePercentage"));
            }

        } catch (SQLException e) {
            System.err.println("error accessing loser 2nd serve percentage: " + e);
        }

        return lSecond;
    }

    /**
     * Method that takes the losers break points faced - break points saved
     * to work out the amount of service breaks against them.
     *
     * @return lBroken, an ArrayList of the amount of service breaks
     */
    @Override
    public ArrayList<Integer> lServiceBreaksAgainst(String tableName) {

        ArrayList<Integer> lBroken = new ArrayList<>();

        String lBrokenSQL = "SELECT CAST(l_bpfaced AS FLOAT) - CAST(l_bpsaved AS FLOAT) AS lBreaks FROM " + tableName + ";";

        try (Connection conn = ConnectDB.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(lBrokenSQL)) {

            while (rs.next()) {
                lBroken.add(rs.getInt("lBreaks"));
            }

        } catch (SQLException e) {
            System.err.println("error accessing loser break points lost: " + e);
        }

        return lBroken;
    }

}
