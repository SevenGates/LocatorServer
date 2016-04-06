package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * * * Java program to connect to MySQL Server database running on localhost, *
 * using JDBC type 4 driver. * * @author http://java67.blogspot.com
 */

public class MySQLTest {
	public static void main(String args[]) {
		String dbURL = "jdbc:mysql://localhost:3306/locatormah?useSSL=false";
		String username = "root";
		String password = "k5!298E45H";
		Connection dbCon = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = "select * from test";
		try {
			// getting database connection to MySQL server
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			System.out.println("Class funkar");
			dbCon = DriverManager.getConnection(dbURL, username, password);
			System.out.println("Driver funkar");
			// getting PreparedStatment to execute query
			stmt = dbCon.prepareStatement(query);
			// Resultset returned by query
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				int count = rs.getInt(1);
				System.out.println("count of stock : " + count);
			}
		} catch (SQLException ex) {
			System.out.println(ex.toString());
			System.out.println("Någor har gått fel.");
	//		Logger.getLogger(CollectionTest.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		} finally {
			// close connection ,stmt and resultset here
		}
	}
}