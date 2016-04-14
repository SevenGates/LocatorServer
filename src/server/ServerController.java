package server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Base64.Encoder;
import javax.imageio.ImageIO;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerController {
	Server server;
	DBCommunicator dbCom;

	public ServerController(Server server) {
		this.server = server;
		dbCom = new DBCommunicator();
	}

	public void msgFromClient(String request) {
		// Gör om till SQL anrop. Och skickar till DB.
		String sqlQuery = request; // Här ska det göras om till anrop.
		String[] splitQuery = sqlQuery.split(",");
		if (splitQuery[0].equals("GCO")) {
			createSQL_GCO(splitQuery[1]);
		} else if (splitQuery[0].equals("CNF")) {
			createSQL_CNF(splitQuery[1]);
		} else if (splitQuery[0].equals("SER")) {
			createSQL_SER(splitQuery);
		} else if (splitQuery[0].equals("SEP")) {
			createSQL_SEP(splitQuery);
		}

		String GET_COMPLEX = "GCO,"; // följs av minst 2st bokstäver som ska
										// skicka anrop till SQL.
		String CONFIRM_COMPLEX = "CNF,"; // följs av plats men valt exempel
											// MalmöHögskola. returnerar true or
											// false.
		String SEARCH_ROOM = "SER,"; // följsa av roomid + vilken plats
		String SEARCH_PROGRAM = "SEP,"; // följs av porgramid + vilken plats

	}

	private void createSQL_SEP(String[] splitQuery) {
		// RÖR INTE I SPRINT 2. 8=====D

	}

	private void createSQL_SER(String[] splitQuery) {

		String SERQuery = "SELECT name, path, floors, id, map, roomid, roomCoor, doorCoor, corridorCoor " + "FROM "
				+ splitQuery[2] + ".building " + "JOIN levels " + "ON building.name=levels.building " + "JOIN room "
				+ "ON levels.id = room.levels " + "WHERE roomid = '" + splitQuery[1] + "'";

		String[] fromDB;
		try {
			fromDB = dbCom.dBSearchRoom(SERQuery);
			createJSON(fromDB);
		} catch (SQLException | JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	private void createSQL_GCO(String splitQuery) {
		String GCOQuery = "SELECT place FROM locatormain.places WHERE place LIKE '%" + splitQuery + "%';";

		JSONObject jsonGCOQuery = null;
		sendCompleteJSONToClient(jsonGCOQuery);

	}
	
	private void createSQL_CNF(String splitQuery) {
		String CNFQuery = "SELECT count(1) FROM locatormah.places WHERE place = '" + splitQuery + "';";
		// lägga till så vi vet om det är false och sånt mannen.

		sendBoolToClient(false);

	}

	private void createJSON(String[] fromDB) throws IOException, JSONException {

		fromDB[1] = stringToByte(fromDB[1]);
		fromDB[4] = stringToByte(fromDB[4]);

		String jsonText = "{\"name\": \" " + fromDB[0] + "\",\"path\": \" " + fromDB[1] + "\"," + "\"floors\": \" "
				+ fromDB[2] + "\",\"id\": \" " + fromDB[3] + "\",\"map\": \" " + fromDB[4] + "\"," + "\"roomid\": \" "
				+ fromDB[5] + "\",\"roomCoor\": \" " + fromDB[6] + "\", \"doorCoor\": \" " + fromDB[7] + "\","
				+ "\"corridorCoor\": \" " + fromDB[8] + "\",}";

		JSONObject obj = new JSONObject(jsonText);
		
		sendCompleteJSONToClient(obj);

	}

	private String stringToByte(String pic) throws IOException {
		File fnew = new File(pic);
		Encoder en = Base64.getEncoder();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedImage originalImage = ImageIO.read(fnew);
		ImageIO.write(originalImage, "BMP", baos);
		baos.flush();
		byte[] buffer = baos.toByteArray();
		String byteToString = en.encodeToString(buffer);

		return byteToString;

	}

	private void sendCompleteJSONToClient(JSONObject jsonGCOQuery) {
		// TODO Auto-generated method stub

	}

	private void sendBoolToClient(boolean b) {
		// TODO Auto-generated method stub

	}

	private void checkContent(String[] fromDB) {
		// kontrollera innehållet
		if (fromDB[0] == "Error") {
			errorHandler(fromDB[1]);
		} else {
			server.getFromController(fromDB);
		}
	}

	private void errorHandler(String fromDB) {
		// Tar emot vilken typ av error. Sedan gör vi något coolt med det.
	}

}
