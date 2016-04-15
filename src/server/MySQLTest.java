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
		String view = null;
		String dbURL = "jdbc:mysql://localhost:3306/locatormah?useSSL=false";
		String username = "root";
		String password = "k5!298E45H";
		Connection dbCon = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = "select room from building";
		try {
			// getting database connection to MySQL server
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			dbCon = DriverManager.getConnection(dbURL, username, password);
			// getting PreparedStatment to execute query
			stmt = dbCon.prepareStatement(query);
			// Resultset returned by query
			rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				
				String name = rs.getString(1);
//				int floors = rs.getInt(2);
//				view = rs.getString(3);
				
				
				
				System.out.println("name : " + name);
//				System.out.println("floors : " + floors);
//				System.out.println("bytes : " + view + "\n");
				
			}
			Server server = new Server();
			
		} catch (SQLException ex) {
			System.out.println(ex.toString());
			
			String[] fel = new String[3];
			fel[0] = "Error";
			fel[1] = "C:/";
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