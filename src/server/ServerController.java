package server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import javax.imageio.ImageIO;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerController implements Serializable{
	Server server;
	DBCommunicator dbCom;

	public ServerController(Server server) {
		this.server = server;
		dbCom = new DBCommunicator();
	}

	public int msgFromClient(String request) throws SQLException, JSONException, IOException {
		// Gör om till SQL anrop. Och skickar till DB.
		String sqlQuery = request; // Här ska det göras om till anrop.
		String[] splitQuery = sqlQuery.split(",");
		if (splitQuery[0].equals("GCO")) {
			createSQL_GCO(splitQuery[1]);
			return 2;
		} else if (splitQuery[0].equals("CNF")) {
			createSQL_CNF(splitQuery[1]);
			return 1;
		} else if (splitQuery[0].equals("SER")) {
			createSQL_SER(splitQuery);
			return 1;
		} else if (splitQuery[0].equals("SEP")) {
			createSQL_SEP(splitQuery);
			return 1;
		}
		return 0;
	}

	private void createSQL_SEP(String[] splitQuery) {
		// RÖR INTE I SPRINT 2. 8=====D

	}

	private void createSQL_SER(String[] splitQuery) throws SQLException, JSONException, IOException {
		splitQuery[2] = changeDB(splitQuery[2]);
		
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

	private void createSQL_GCO(String splitQuery) throws SQLException, JSONException, IOException {
		String GCOQuery = "SELECT place FROM locatormain.places WHERE place LIKE '"+ splitQuery +"%';";
		ArrayList<String> differentPlaces = dbCom.searchComplex(GCOQuery);
		
		createJSON_GCO(differentPlaces);
	}
	
	private void createSQL_CNF(String splitQuery) throws SQLException, JSONException, IOException {
		String CNFQuery = "SELECT count(1) FROM locatormain.places WHERE place = '" + splitQuery + "';";
		boolean finalBool = dbCom.confirmComplex(CNFQuery);

		sendBoolToClient(finalBool);

	}

	private String changeDB(String string) throws SQLException, JSONException, IOException {
		String query = "SELECT dbname FROM locatormain.places WHERE place LIKE '" + string + "';";;
		String newString = dbCom.dBchange(query);
				return newString;
	}
	
	private void createJSON(String[] fromDB) throws IOException, JSONException {
		fromDB[1] = stringToByte(fromDB[1]);
		fromDB[4] = stringToByte(fromDB[4]);

		String jsonText = "{\"name\": \"" + fromDB[0] + "\",\"path\": \"" + fromDB[1] + "\"," + "\"floors\": \""
				+ fromDB[2] + "\",\"id\": \"" + fromDB[3] + "\",\"map\": \"" + fromDB[4] + "\"," + "\"roomid\": \""
				+ fromDB[5] + "\",\"roomCoor\": \"" + fromDB[6] + "\", \"doorCoor\": \"" + fromDB[7] + "\","
				+ "\"corridorCoor\": \"" + fromDB[8] + "\",}";

		JSONObject obj = new JSONObject(jsonText);
		sendCompleteJSONToClient(obj);

	}
	
	private void createJSON_GCO(ArrayList<String> differentPlaces) throws IOException, JSONException {
		String jsonText = "{\"nbrOfPlaces\": \"" + Integer.toString(differentPlaces.size());
		String jsonBuildText = "";
		String jsonCloseText = "\",}";
		
		if (differentPlaces.size()>0){
			for (int i = 0; i < differentPlaces.size(); i++){
				jsonBuildText += "\",\"place" + i+1 + "\": \"" + differentPlaces.get(i);
			}
			jsonBuildText += jsonCloseText;
			jsonText += jsonBuildText;
		} else{
			jsonText += jsonCloseText;
		}

		JSONObject obj = new JSONObject(jsonText);
		
		sendCompleteJSONToClient(obj);

	}
	
	private String stringToByte(String pic) throws IOException {
		File fnew = new File(pic);
		Encoder en = Base64.getEncoder();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedImage originalImage = ImageIO.read(fnew);
		System.out.println("Efter buffered image " + originalImage.toString());
		ImageIO.write(originalImage, "BMP", baos);
		byte[] buffer = baos.toByteArray();
		String byteToString = en.encodeToString(buffer);
		baos.flush();
		baos.close();
		return byteToString;

	}

	private void sendCompleteJSONToClient(JSONObject jsonCool) {
		server.sendJsonToClient(jsonCool);

	}

	private void sendBoolToClient(boolean b) {
		server.sendBool(b);
	}

	private void errorHandler(String fromDB) {
		// Tar emot vilken typ av error. Sedan gör vi något coolt med det.
	}

//	public static void main (String[] args) throws SQLException, JSONException, IOException{
//		ServerController con = new ServerController();
//		con.msgFromClient("SER,ORD131,Malmö Högskola");
//	}
}
