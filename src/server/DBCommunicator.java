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

	public String[] query(String sqlQuery) {
		// anropar DB. Får tillbaka data i form av strängar. Som vi lagrar i en
		// sträng array. Och sedan returnerar.
		String[] resultFromDB = new String[11]; // En string array som just nu
												// kan hålla all data från alla
												// tabeller (tillsammans 11st)
		return resultFromDB;
	}

	public void tableRoom(String roomSearch) throws SQLException, JSONException, IOException {
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
		String rBuilding = "inget frånDB";
		String rBuildingMap = "inget från DB";
		String rLevelMap = "inget från DB";
		String rNbrLevels = "inget från DB";
		String query = "SELECT levels, roomid, roomCoor, "
				+ "doorCoor, corridorCoor, building, buildingMap, levelMap, nbrLevels " + "FROM " + dbName + ".room "
				+ "WHERE roomid='" + roomSearch + "'";

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
				rBuilding = rs.getString("building");
				rBuildingMap = rs.getString("buildingMap");
				rLevelMap = rs.getString("levelMap");
				rNbrLevels = rs.getString("nbrLevels");
			}
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

		File fnew = new File(rBuildingMap);
		File fnew2 = new File(rLevelMap);
		Encoder en = Base64.getEncoder();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedImage originalImage = ImageIO.read(fnew);
		ImageIO.write(originalImage, "BMP", baos);
		baos.flush();
		BufferedImage image2 = ImageIO.read(fnew2);
		byte[] buffer = baos.toByteArray();
		String byteToString = en.encodeToString(buffer);
		ImageIO.write(image2, "BMP", baos);
		baos.flush();
		byte[] buffer2 = baos.toByteArray();
		String byteToString2 = en.encodeToString(buffer);
		
		
		String jsonText = "{\"levels\": \" " + rLevels + "\",\"roomid\": \" " + rRoomid + "\"," + "\"roomCoor\": \" "
				+ rRoomCoor + "\",\"doorCoor\": \" " + rDoorCoor + "\",\"corridorCoor\": \" " + rCorridorCoor + "\","
				+ "\"building\": \" " + rBuilding + "\",\"buildingMap\": \" " + byteToString + "\", \"levelMap\": \" "
				+ byteToString2 + "\"," + "\"nbrLevels\": \" " + rNbrLevels + "\",}";

		JSONObject obj = new JSONObject(jsonText);
//		String ab = obj.getString("levels");
//		String abc = obj.getString("roomid");
//		String abcd = obj.getString("roomCoor");
//		String abcde = obj.getString("doorCoor");
//		String abcdef = obj.getString("corridorCoor");
//		String abcdefg = obj.getString("building");
//		String abcdefgh = obj.getString("buildingMap");
//		String abcdefghi = obj.getString("levelMap");
//		String abcdefghij = obj.getString("nbrLevels");
//
//		System.out.println(ab + " " + abc + " " + abcd + " " + abcde + " " + abcdef + " " + abcdefg + " " + abcdefgh
//				+ " " + abcdefghi + " " + abcdefghij);

		// String name, floors, path; // Tablen building
		// String id, building, map; // Tablen levels
		// String roomid, roomCoor, doorCoor, corridorCoor, levels; // Tablen
		// room

	}

	public static void main(String[] args) throws SQLException, JSONException, IOException {
		DBCommunicator db = new DBCommunicator();
		db.tableRoom("NIB0517");
	}
}

/*
 * Vid felanrop till DB tycker jag och Isak att vi ska skapa en string[] med
 * info om felkoden som returneras ändå. Utifrån den kan vi i controllern se
 * vilket fel och skicka en standardbild till klienten och kort info. Ex.
 * String[0] = "error". String[1] = "SQLexcep" eventuellt fler positioner med
 * ytterliggare information
 */