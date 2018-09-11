package Database;

import Utils.DBUtils;
import Utils.RandomNumberGenerator;
import java.sql.*;
import java.util.*;

/**
 * Class contains the method to standardise the non-numeric
 * data into arbitrary float values between 0 and 1. All
 * distinct values in a particular column will be assigned
 * a random float value.
 *
 * @author Alex Kelly
 */
public class StandardiseNonNumeric {

    /**
     * Method for standardising non-numeric column with
     * arbitrary normalised value (between 0-1). This
     * method is for the non numeric columns that have a
     * winner and loser column.
     *
     * @param colName the column being standardised
     */
    public void standardiseWinnerLoserCol(String colName) {

        if (!(colName.equals("ioc") || colName.equals("hand"))) {
            throw new IllegalArgumentException("invalid col name '" + colName + "'");
        }

        String loserColName = "loser_"+colName;
        String winnerColName = "winner_"+colName;

        //add distinct values to sets so as duplicates aren't allowed
        Set<String> colValuesSet = new HashSet<>();
        Set<Float> colFloatSet = new HashSet<>();

        String selectW = "SELECT DISTINCT " + winnerColName + " FROM training_data;";
        String selectL = "SELECT DISTINCT " + loserColName + " FROM training_data;";

        //establish connection and create rs of distinct values
        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmtSelectW = conn.prepareStatement(selectW);
             ResultSet rsw = pstmtSelectW.executeQuery();
             PreparedStatement pstmtSelectL = conn.prepareStatement(selectL);
             ResultSet rsl = pstmtSelectL.executeQuery()) {

            //store distinct values in AL
            while (rsw.next()) {
                colValuesSet.add(rsw.getString(winnerColName));
//                System.out.println(rs.getString(winnerColName)); //TEST *******
            }

            while (rsl.next()) {
                colValuesSet.add(rsl.getString(loserColName));
//                System.out.println(rs.getString(loserColName)); //TEST *******
            }

            //create a random float for each distinct value
            int size = colValuesSet.size();
            int x = 0;
            while (x < size) {
                float r = RandomNumberGenerator.randomise();
                colFloatSet.add(r);
                x++;
            }

            List colValuesList = new ArrayList(colValuesSet);
            List colFloatList = new ArrayList(colFloatSet);

            /*
             * execute query to update all entries with the same value
             * as col.get(i) in db table with the arbitrary float assigned
             */
            for (int i = 0; i < size; i++) {
                String updateColW = "UPDATE training_data SET " + winnerColName + " = '" + colFloatList.get(i) +
                        "' WHERE " + winnerColName + " = '" + colValuesList.get(i) + "';";
                String updateColL = "UPDATE training_data SET " + loserColName + " = '" + colFloatList.get(i) +
                        "' WHERE " + loserColName + " = '" + colValuesList.get(i) + "';";

                System.out.println(colValuesList.get(i) + " = " + colFloatList.get(i)); //test

                try (PreparedStatement pstmtUpdateW = conn.prepareStatement(updateColW);
                     PreparedStatement pstmtUpdateL = conn.prepareStatement(updateColL)) {

                    pstmtUpdateW.executeUpdate();
                    pstmtUpdateL.executeUpdate();

                } catch (SQLException e) {
                    System.err.println("Error standardising " + colName + ":" + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.out.println("Error standardising " + colName + ":" + e);
        }
        System.out.println("column -> " + loserColName + " and " + winnerColName + " standardised to arbitrary float values between 0-1");
    }

    /**
     * Method for standardising non-numeric column with
     * arbitrary normalised value (between 0-1). This
     * method is for the non numeric columns that do not
     * have a winner and loser column.
     *
     * @param colName the column being standardised
     */
    public void standardiseIndividualCol(String colName) {

        removeApostrophes();

        String[] cols = DBUtils.standardiseGenColCols();
        if (!(Arrays.asList(cols).contains(colName))) {
            throw new IllegalArgumentException("invalid col name '" + colName + "', options are " + Arrays.toString(cols));
        }

        List<String> col = new ArrayList<>();
        List<Float> colFloat = new ArrayList<>();

        String select = "SELECT DISTINCT " + colName + " FROM training_data;";

        //establish connection and create rs of distinct values
        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmtSelect = conn.prepareStatement(select);
             ResultSet rs = pstmtSelect.executeQuery()) {

            //store distinct values in AL
            while (rs.next()) {
                col.add(rs.getString(colName));
//                System.out.println(rs.getString(colName)); //TEST *******
            }

            //create a random float for each distinct value
            int size = col.size();
            int x = 0;
            while (x < size) {
                float r = RandomNumberGenerator.randomise();
                colFloat.add(r);
                x++;
            }

            /*
             * execute query to update all entries with the same value
             * as col.get(i) in db table with the arbitrary float assigned
             */
            for (int i = 0; i < size; i++) {
                String updateColW = "UPDATE training_data SET " + colName + " = '" + colFloat.get(i) +
                        "' WHERE " + colName + " = '" + col.get(i) + "';";

                System.out.println(col.get(i) + " = " + colFloat.get(i)); //test

                try (PreparedStatement pstmtUpdateW = conn.prepareStatement(updateColW)) {

                    pstmtUpdateW.executeUpdate();

                } catch (SQLException e) {
                    System.err.println("Error standardising " + colName + ":" + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.out.println("Error standardising " + colName + ":" + e);
        }
        System.out.println("column -> " + colName + " standardised to arbitrary float value between 0-1");
    }

    /**
     * Helper method to modify the entries within the tourney_name that
     * contain apostrophes.
     */
    private void removeApostrophes() {

        String updateQSQL = "UPDATE training_data SET tourney_name = 'Queens Club' WHERE tourney_name LIKE 'Queen_s Club';";
        String updateHSQL = "UPDATE training_data SET tourney_name = 'S-Hertogenbosch' WHERE tourney_name LIKE '_S-Hertogenbosch';";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmtUpdateQ = conn.prepareStatement(updateQSQL);
             PreparedStatement pstmtUpdateH = conn.prepareStatement(updateHSQL);) {

            pstmtUpdateQ.executeUpdate();
            pstmtUpdateH.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error removing apostrophes: " + e.getMessage());
            e.printStackTrace();
        }
    }


//    /**
//     * Method for standardising the non-numeric surface into a random float between
//     * 0 and 1.
//     *
//     * @param tableName the database table being modified
//     */
//    private void standardiseSurface(String tableName) {
//
//        if (!(tableName.equals("training_data") || tableName.equals("test_data"))) {
//            throw new IllegalArgumentException();
//        }
//
//        float grass = RandomNumberGenerator.randomise();
//        String grassSQL = "UPDATE " + tableName + " SET surface = '" + grass + "' WHERE surface = 'Grass';";
//
//        float carpet = RandomNumberGenerator.randomise();
//        String carpetSQL = "UPDATE " + tableName + " SET surface = '" + carpet + "' WHERE surface = 'Carpet';";
//
//        float clay = RandomNumberGenerator.randomise();
//        String claySQL = "UPDATE " + tableName + " SET surface = '" + clay + "' WHERE surface = 'Clay';";
//
//        float hard = RandomNumberGenerator.randomise();
//        String hardSQL = "UPDATE " + tableName + " SET surface = '" + hard + "' WHERE surface = 'Hard';";
//
//        try (Connection conn = ConnectDB.connect()) {
//
//            PreparedStatement pstmt1 = conn.prepareStatement(grassSQL);
//            pstmt1.executeUpdate();
//
//            PreparedStatement pstmt2 = conn.prepareStatement(carpetSQL);
//            pstmt2.executeUpdate();
//
//            PreparedStatement pstmt3 = conn.prepareStatement(claySQL);
//            pstmt3.executeUpdate();
//
//            PreparedStatement pstmt4 = conn.prepareStatement(hardSQL);
//            pstmt4.executeUpdate();
//
//        } catch (SQLException e) {
//            System.out.println("Error standardising surfaces: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Method for standardising the non-numeric tourney_level into a random float between
//     * -1 and 1.
//     *
//     * @param tableName the database table being modified
//     */
//    private void standardiseTourneyLevel(String tableName) {
//
//        if (!(tableName.equals("training_data") || tableName.equals("test_data"))) {
//            throw new IllegalArgumentException();
//        }
//
//        float g = RandomNumberGenerator.randomise();
//        String gSQL = "UPDATE " + tableName + " SET tourney_level = '" + g + "' WHERE tourney_level = 'G';";
//
//        float m = RandomNumberGenerator.randomise();
//        String mSQL = "UPDATE " + tableName + " SET tourney_level = '" + m + "' WHERE tourney_level = 'M';";
//
//        float f = RandomNumberGenerator.randomise();
//        String fSQL = "UPDATE " + tableName + " SET tourney_level = '" + f + "' WHERE tourney_level = 'F';";
//
//        float d = RandomNumberGenerator.randomise();
//        String dSQL = "UPDATE " + tableName + " SET tourney_level = '" + d + "' WHERE tourney_level = 'D';";
//
//        float a = RandomNumberGenerator.randomise();
//        String aSQL = "UPDATE " + tableName + " SET tourney_level = '" + a + "' WHERE tourney_level = 'A';";
//
//        float c = RandomNumberGenerator.randomise();
//        String cSQL = "UPDATE " + tableName + " SET tourney_level = '" + c + "' WHERE tourney_level = 'C';";
//
//        try (Connection conn = ConnectDB.connect()) {
//
//            PreparedStatement pstmtG = conn.prepareStatement(gSQL);
//            pstmtG.executeUpdate();
//
//            PreparedStatement pstmtM = conn.prepareStatement(mSQL);
//            pstmtM.executeUpdate();
//
//            PreparedStatement pstmtF = conn.prepareStatement(fSQL);
//            pstmtF.executeUpdate();
//
//            PreparedStatement pstmtD = conn.prepareStatement(dSQL);
//            pstmtD.executeUpdate();
//
//            PreparedStatement pstmtA = conn.prepareStatement(aSQL);
//            pstmtA.executeUpdate();
//
//            PreparedStatement pstmtC = conn.prepareStatement(cSQL);
//            pstmtC.executeUpdate();
//
//        } catch (SQLException e) {
//            System.err.println("Error standardising tourney_level: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Method for standardising the non-numeric winner/loser_seed into a random
//     * float between -1 and 1.
//     *
//     * @param tableName the database table being modified
//     * @param winnerOrLoser specify which player seed to standardise (match winner or loser)
//     */
//    private void standardiseSeed(String tableName, String winnerOrLoser) {
//
//        if (!(tableName.equals("training_data") || tableName.equals("test_data"))) {
//            throw new IllegalArgumentException();
//        }
//
//        if (!(winnerOrLoser.equals("winner") || winnerOrLoser.equals("loser"))) {
//            throw new IllegalArgumentException();
//        }
//
//        float unseeded = RandomNumberGenerator.randomise();
//
//        String unseededSQL = "UPDATE " + tableName + " SET " + winnerOrLoser + "_seed = '" + unseeded + "' WHERE "
//                + winnerOrLoser + "_seed ='';";
//
//        try (Connection conn = ConnectDB.connect()) {
//
//            PreparedStatement pstmt = conn.prepareStatement(unseededSQL);
//            pstmt.executeUpdate();
//
//        } catch (SQLException e) {
//            System.err.println("Error standardising winner_seed: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Method for standardising the non-numeric winner_entry into a random float between
//     *  -1 and 1.
//     *
//     *  @param tableName the database table being modified
//     *  @param winnerOrLoser specify which player entry to standardise (match winner or loser)
//     */
//    public void standardiseEntry(String tableName, String winnerOrLoser) {
//
//        if (!(tableName.equals("training_data") || tableName.equals("test_data"))) {
//            throw new IllegalArgumentException();
//        }
//
//            if (!(winnerOrLoser.equals("winner") || winnerOrLoser.equals("loser"))) {
//            throw new IllegalArgumentException();
//        }
//
//        float wc = RandomNumberGenerator.randomise();
//        String wcSQL = "UPDATE " + tableName + " SET " + winnerOrLoser + "_entry = '" + wc + "' WHERE "
//                + winnerOrLoser + "_entry = 'WC';";
//
//        float ll = RandomNumberGenerator.randomise();
//        String llSQL = "UPDATE " + tableName + " SET " + winnerOrLoser + "_entry = '" + ll + "' WHERE "
//                + winnerOrLoser + "_entry = 'LL';";
//
//        float q = RandomNumberGenerator.randomise();
//        String qSQL = "UPDATE " + tableName + " SET " + winnerOrLoser + "_entry = '" + q + "' WHERE "
//                + winnerOrLoser + "_entry = 'Q';";
//
//        float pr = RandomNumberGenerator.randomise();
//        String prSQL = "UPDATE " + tableName + " SET " + winnerOrLoser + "_entry = '" + pr + "' WHERE "
//                + winnerOrLoser + "_entry = 'WC';";
//
//        float invite = RandomNumberGenerator.randomise();
//        String inviteSQL = "UPDATE " + tableName + " SET " + winnerOrLoser + "_entry = '" + invite + "' WHERE "
//                + winnerOrLoser + "_entry = 'WC';";
//
//        float se = RandomNumberGenerator.randomise();
//        String seSQL = "UPDATE " + tableName + " SET " + winnerOrLoser + "_entry = '" + se + "' WHERE "
//                + winnerOrLoser + "_entry = 'WC';";
//
//        try (Connection conn = ConnectDB.connect()) {
//
//            PreparedStatement pstmtWC = conn.prepareStatement(wcSQL);
//            pstmtWC.executeUpdate();
//
//            PreparedStatement pstmtLL = conn.prepareStatement(llSQL);
//            pstmtLL.executeUpdate();
//
//            PreparedStatement pstmtQ = conn.prepareStatement(qSQL);
//            pstmtQ.executeUpdate();
//
//            PreparedStatement pstmtPR = conn.prepareStatement(prSQL);
//            pstmtPR.executeUpdate();
//
//            PreparedStatement pstmtInvite = conn.prepareStatement(inviteSQL);
//            pstmtInvite.executeUpdate();
//
//            PreparedStatement pstmtSE = conn.prepareStatement(seSQL);
//            pstmtSE.executeUpdate();
//
//        } catch (SQLException e) {
//            System.err.println("Error standardising entry: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Method for standardising the non-numeric winner_hand into a random float between
//     *  -1 and 1.
//     *
//     *  @param tableName the database table being modified
//     *  @param winnerOrLoser specify which players' hand to standardise (match winner or loser)
//     */
//    public void standardiseHand(String tableName, String winnerOrLoser) {
//
//        if (!(tableName.equals("training_data") || tableName.equals("test_data"))) {
//            throw new IllegalArgumentException();
//        }
//
//        if (!(winnerOrLoser.equals("winner") || winnerOrLoser.equals("loser"))) {
//            throw new IllegalArgumentException();
//        }
//
//        float l = RandomNumberGenerator.randomise();
//        String lSQL = "UPDATE " + tableName + " SET " + winnerOrLoser + "_hand = '" + l + "' WHERE "
//                + winnerOrLoser + "_hand = 'L';";
//
//        float r = RandomNumberGenerator.randomise();
//        String rSQL = "UPDATE " + tableName + " SET " + winnerOrLoser + "_hand = '" + r + "' WHERE "
//                + winnerOrLoser + "_hand = 'R';";
//
//        try (Connection conn = ConnectDB.connect()) {
//
//            PreparedStatement pstmtL = conn.prepareStatement(lSQL);
//            pstmtL.executeUpdate();
//
//            PreparedStatement pstmtR = conn.prepareStatement(rSQL);
//            pstmtR.executeUpdate();
//
//        } catch (SQLException e) {
//            System.err.println("Error standardising winner_hand: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Method for standardising the winner/loser_ioc to a random
//     * float within the range -1 to 1.
//     *
//     *  @param tableName the database table being modified
//     *  @param winnerOrLoser specify which player ioc to standardise (match winner or loser)
//     */
//    private void standardiseIOC(String tableName, String winnerOrLoser) {
//
//        if (!(tableName.equals("training_data") || tableName.equals("test_data"))) {
//            throw new IllegalArgumentException();
//        }
//
//        if (!(winnerOrLoser.equals("winner") || winnerOrLoser.equals("loser"))) {
//            throw new IllegalArgumentException();
//        }
//
//        ArrayList<String> ioc = new ArrayList<>();
//        ArrayList<Float> iocFloat = new ArrayList<>();
//
//        String selectIOC = "SELECT DISTINCT " + winnerOrLoser + "_ioc FROM " + tableName;
//
//        //establish connection and create rs of distinct ioc values
//        try (Connection conn = ConnectDB.connect()) {
//
//             PreparedStatement pstmtSelect = conn.prepareStatement(selectIOC);
//             ResultSet rs = pstmtSelect.executeQuery();
//
//            //store distinct ioc values in AL
//            while (rs.next()) {
//                ioc.add(rs.getString(winnerOrLoser + "_ioc"));
//            }
//
//            //create a random float for each distinct ioc value
//            int size = ioc.size();
//            int x = 0;
//            while (x < size) {
//                float r = RandomNumberGenerator.randomise();
//                iocFloat.add(r);
//                x++;
//            }
//
//            //execute query to update values in db table with random floats
//            for (int i = 0; i < size; i++) {
//                String updateIOC = "UPDATE " + tableName + " SET " + winnerOrLoser + "_ioc = '" + iocFloat.get(i) +
//                        "' WHERE " + winnerOrLoser + "_ioc = '" + ioc.get(i) + "';";
//
//                try {
//                    PreparedStatement pstmtUpdate = conn.prepareStatement(updateIOC);
//                    pstmtUpdate.executeUpdate();
//
//                } catch (SQLException e) {
//                    System.err.println("Error standardising " + winnerOrLoser + " ioc: " + e.getMessage());
//                }
//            }
//
//        } catch (SQLException e) {
//            System.out.println("Error standardising ioc: " + e);
//        }
//    }
//
//    private void standardiseTourneyID(String tableName) {
//
//        if (!(tableName.equals("training_data") || tableName.equals("test_data"))) {
//            throw new IllegalArgumentException();
//        }
//
//        ArrayList<String> tourneyID = new ArrayList<>();
//        ArrayList<Float> tourneyIDFloat = new ArrayList<>();
//
//        String selectIOC = "SELECT DISTINCT tourney_id FROM " + tableName;
//
//        //establish connection and create rs of distinct id values
//        try (Connection conn = ConnectDB.connect();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(selectIOC)) {
//
//            //store distinct ioc values in AL
//            while (rs.next()) {
//                tourneyID.add(rs.getString("tourney_id"));
//            }
//
//            //create a random float for each distinct id value
//            int size = tourneyID.size();
//            int x = 0;
//            while (x < size) {
//                float r = RandomNumberGenerator.randomise();
//                tourneyIDFloat.add(r);
//                x++;
//            }
//
//            //execute query to update values in db table with random floats
//            for (int i = 0; i < size; i++) {
//                String updateIOC = "UPDATE " + tableName + " SET tourney_id = '" + tourneyIDFloat.get(i) +
//                        "' WHERE tourney_id = '" + tourneyID.get(i) + "';";
//
//                try {
//                    PreparedStatement pstmt = conn.prepareStatement(updateIOC);
//                    pstmt.executeUpdate();
//
//                } catch (SQLException e) {
//                    System.err.println("Error standardising tourney_id: " + e.getMessage());
//                }
//            }
//
//        } catch (SQLException e) {
//            System.out.println("Error standardising tourney_id: " + e);
//        }
//    }

}