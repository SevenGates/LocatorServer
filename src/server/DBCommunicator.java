package server;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Base64.Encoder;
import javax.imageio.ImageIO;
import org.json.JSONException;
import org.json.JSONObject;
import com.mysql.jdbc.Statement;

public class DBCommunicator {

	
	public String[] dBSearchRoom(String roomSearch) throws SQLException, JSONException, IOException {
		Statement stmt = null;
		Connection con = null;
		String dbURL = "jdbc:mysql://localhost:3306/locatormah?useSSL=false";
		String username = "root";
		String password = "k5!298E45H";
		String query = roomSearch;
		String[] array = new String[9];

		try {
			con = DriverManager.getConnection(dbURL, username, password);
			stmt = (Statement) con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				array[0] = rs.getString("name");
				array[1] = rs.getString("path");
				array[2] = rs.getString("floors");
				array[3] = rs.getString("id");
				array[4] = rs.getString("map");
				array[5] = rs.getString("roomid");
				array[6] = rs.getString("roomCoor");
				array[7] = rs.getString("doorCoor");
				array[8] = rs.getString("corridorCoor");
			
			}
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		
		return array;
	}
	
	public String dBchange(String string) throws SQLException, JSONException, IOException {
		Statement stmt = null;
		Connection con = null;
		String dbURL = "jdbc:mysql://localhost:3306/locatormain?useSSL=false";
		String username = "root";
		String password = "k5!298E45H";
		String query = string;
		String result = null;
		try {
			con = DriverManager.getConnection(dbURL, username, password);
			stmt = (Statement) con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				result = rs.getString("dbname");
			}
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		
		return result;
	}


}

/*
 * Vid felanrop till DB tycker jag och Isak att vi ska skapa en string[] med
 * info om felkoden som returneras ändå. Utifrån den kan vi i controllern se
 * vilket fel och skicka en standardbild till klienten och kort info. Ex.
 * String[0] = "error". String[1] = "SQLexcep" eventuellt fler positioner med
 * ytterliggare information
 */