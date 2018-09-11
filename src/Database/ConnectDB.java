package Database;

import java.sql.*;

/**
 * Class that contains variables required and method for connecting
 * to postgresql database. JAR file included in project build path.
 *
 * @author Alex Kelly
 */
public class ConnectDB {

    private final static String DB_NAME = "data";
    private final static String URL = "jdbc:postgresql://localhost:5432/" + DB_NAME;
    private final static String USER = "alex";
    private final static String  PASSWORD = "password";

    /**
     * Method for establishing a connection to the database.
     *
     * @return a connection object.
     */
    public static Connection connect() {

        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
//            System.out.println("connection established to " + DB_NAME);
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }

        return conn;
    }

//    /**
//     * Main method to test connection.
//     */
//    public static void main(String[] args) {
//        ConnectDB c = new ConnectDB();
//        c.connect();
//    }

}
