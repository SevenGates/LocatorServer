package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.Statement;


public class DBCommunicator {
	

	public String[] query(String sqlQuery) {
		// anropar DB. Får tillbaka data i form av strängar. Som vi lagrar i en sträng array. Och sedan returnerar.
		String[] resultFromDB = new String[11]; // En string array som just nu kan hålla all data från alla tabeller (tillsammans 11st)
		return resultFromDB; 
	}
	
	
	public void tableRoom(String roomSearch) throws SQLException, JSONException {
		 Statement stmt = null;
		 Connection con = null;
		 String dbURL = "jdbc:mysql://localhost:3306/locatormah?useSSL=false";
		 String username = "root";
		 String password = "k5!298E45H";
		 String dbName = "locatormah";
		 String rLevels = "inget från DB";
		 String rRoomid = "inget från DB";
		 String rRoomCoor = "inget från DB";
		 String rDoorCoor = "inget från DB";
		 String rCorridorCoor = "inget från DB";
		 String query =
		        "SELECT levels, roomid, roomCoor, " +
		        "doorCoor, corridorCoor " +
		        "FROM " + dbName + ".room "
		        	+"WHERE roomid='"+roomSearch+"'";
		        

		    try {
		    	con = DriverManager.getConnection(dbURL, username, password);
		        stmt = (Statement) con.prepareStatement(query);
		        ResultSet rs = stmt.executeQuery(query);
		        
		        while (rs.next()) {
		        	rLevels = rs.getString("levels");
		        	rRoomid = rs.getString("roomid");
		        	rRoomCoor = rs.getString("roomCoor");
		        	rDoorCoor = rs.getString("doorCoor");
		        	rCorridorCoor = rs.getString("corridorCoor");
		        }
		    } catch (SQLException e ) {
		        System.out.println(e);
		    } finally {
		        if (stmt != null) { stmt.close(); }
		    }
		    
			
		String spock = "{\"levels\": \" "+ rLevels + "\",\"roomid\": \" " + rRoomid + "\","
				+ "\"roomCoor\": \" " +	rRoomCoor + "\",\"doorCoor\": \" "+ rDoorCoor + "\",\"corridorCoor\": \" "+ rCorridorCoor + "\", }";
		
		JSONObject obj = new JSONObject(spock);
		String ab = obj.getString("levels");
		String abc = obj.getString("roomid");
		String abcd = obj.getString("roomCoor");
		String abcde = obj.getString("doorCoor");
		String abcdef = obj.getString("corridorCoor");
		System.out.println(ab + " " + abc + " " + abcd + " " + abcde + " " + abcdef);
		
		
//		String name, floors, path; // Tablen building
//		String id, building, map; // Tablen levels
//		String roomid, roomCoor, doorCoor, corridorCoor, levels; // Tablen room
		
		
		
	}
	public static void main (String[] args) throws SQLException, JSONException{
		DBCommunicator db = new DBCommunicator();
		db.tableRoom("NIB0517");
	}
} 

/*
 * Vid felanrop till DB tycker jag och Isak att vi ska skapa en string[] med info om felkoden som returneras ändå. 
 * Utifrån den kan vi i controllern se vilket fel och skicka en standardbild till klienten och kort info.
 * Ex. String[0] = "error". String[1] = "SQLexcep" eventuellt fler positioner med ytterliggare information
*/