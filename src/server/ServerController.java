package server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Base64.Encoder;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerController implements Serializable{
	Server server;
	DBCommunicator dbCom;
	MapGraph mp = new MapGraph();
	private HashMap<String,String> map = new HashMap<String, String>();
	private HashMap<String,ArrayList<String>> edgeMap = new HashMap<String,ArrayList<String>>();

	public ServerController(Server server) {
		this.server = server;
		dbCom = new DBCommunicator(this);
	}

	/*
	 * Spliting the message from Client. If the three first letters is one of the IF´s they will be activated.
	 * 
	 */
	public int msgFromClient(String request) throws SQLException, JSONException, IOException {
		String sqlQuery = request;
		server.LOGG("CONTROLLER/msgFromClient: Kontrollerar vad controllern tar emot från klienten = " + request);
		String[] splitQuery = sqlQuery.split(",");
		server.LOGG("CONTROLLER/msgFromClient: En strängarray som innehåller allt splitande av requesten");
		if (splitQuery[0].equals("GCO")) {
			server.LOGG("CONTROLLER/msgFromClient: If 'GCO' så är vi här inne.");
			createSQL_GCO(splitQuery[1]);
			return 1;
		} else if (splitQuery[0].equals("CNF")) {
			server.LOGG("CONTROLLER/msgFromClient: If 'CNF' så är vi här inne.");
			createSQL_CNF(splitQuery[1]);
			return 2;
		} else if (splitQuery[0].equals("SER")) {
			server.LOGG("CONTROLLER/msgFromClient: If 'SER' så är vi här inne.");
			createSQL_SER(splitQuery);
			return 1;
		} else if (splitQuery[0].equals("SEP")) {
			server.LOGG("CONTROLLER/msgFromClient: If 'SEP' så är vi här inne.");
			createSQL_SEP(splitQuery);
			return 1;
		}
		return 0;
	}

	private void createSQL_SEP(String[] splitQuery) {
		server.LOGG("CONTROLLER/createSQL_SEP: ");
		// RÖR INTE I SPRINT 2. 8=====D

	}

	private void createSQL_SER(String[] splitQuery) throws SQLException, JSONException, IOException {
		server.LOGG("CONTROLLER/createSQL_SER: efter anropad metod createSQL_SER");
		String msg = "Det blev fel när du angav rummets namn, försök igen.";
		splitQuery[2] = changeDB(splitQuery[2]);
		server.LOGG("CONTROLLER/createSQL_SER: Har ändrat DB med följande information = " + splitQuery[2]);
		
		String SERQuery = "SELECT name, path, floors, id, map, roomid, roomCoor, doorCoor, corridorCoor " + "FROM "
				+ splitQuery[2] + ".building " + "JOIN levels " + "ON building.name=levels.building " + "JOIN room "
				+ "ON levels.id = room.levels " + "WHERE roomid = '" + splitQuery[1] + "'";
		server.LOGG("CONTROLLER/createSQL_SER: SQLQueryn som är genererad = " + SERQuery);
		String[] fromDB;
		try {
			fromDB = dbCom.dBSearchRoom(SERQuery);
			server.LOGG("CONTROLLER/createSQL_SER: I TRY när fromDB-arrayen ska fyllas med svar från DB. Svar = ");
			if (fromDB.length == 2){
				server.LOGG("CONTROLLER/createSQL_SER: Om fromDB enbart har 2 platser. Då skickas följande meddelande = " + msg);
				errorHandler(msg);
			} else {
				server.LOGG("CONTROLLER/createSQL_SER: Else om fromDB är större än 2 platser. Då skickas till createJSON = ");
				createJSON(fromDB);
			}
		} catch (SQLException | JSONException | IOException e) {
			// TODO Auto-generated catch block
			server.LOGG("CONTROLLER/createSQL_SER: CATCH = " + e);
		}
		
		createSQL_nodes(splitQuery[1], splitQuery[2]);

	}

	private void createSQL_nodes(String room, String dB) throws SQLException, JSONException, IOException {
		String queryGetFloor = "SELECT levels " + "FROM "
				+ dB + ".room " + "WHERE roomid = '" + room + "'";
		String floor = "";
		try {
			floor = dbCom.dBSearchFloor(queryGetFloor);
			
		} catch (SQLException | JSONException | IOException e) {
			server.LOGG("CONTROLLER/createSQL_nodes: CATCH = " + e);
		}
		
		
		String queryGetNodes = "SELECT nID, coor " + "FROM "
				+ dB + ".node " + "WHERE nID LIKE '" + floor + "%'";
		map = dbCom.dBGetNodes(queryGetNodes);
		
		String queryGetEndNode = "SELECT corridorCoor " + "FROM "
				+ dB + ".room " + "WHERE roomid = '" + room + "';";
		String value = dbCom.dBGetEndNode(queryGetEndNode);
		map.put("endNode", value);
		addNodes(map);
		
		for(Entry<String, String> entry : map.entrySet()) {
		    String nID = entry.getKey();
		    createSQL_edge(dB, nID, room);
		}
		
		
	}

	private void createSQL_edge(String dB, String nID, String room) throws SQLException, JSONException, IOException {
		String queryGetEdges = "SELECT connectID " + "FROM "
				+ dB + ".edge " + "WHERE nID = '" + nID + "';";
		ArrayList<String> fromDB = new ArrayList<String>();
		
		try {
			fromDB = dbCom.dBSearchEdges(queryGetEdges);
			if (fromDB.size() > 0){
				for (int i = 0; i < fromDB.size();i++){
					addEdges(nID, fromDB.get(i));
				}
			}
			
		} catch (SQLException e) {
			server.LOGG("CONTROLLER/create: CATCH = " + e);
		}
		
		
		String queryGetEdges2 = "SELECT nID " + "FROM "
				+ dB + ".noderoom " + "WHERE roomID = '" + room + "';";
		ArrayList<String> fromDB2 = new ArrayList<String>();
		
		try {
			fromDB2 = dbCom.dBSearchEdges(queryGetEdges2);
			if (fromDB2.size() > 0){
				for (int i = 0; i < fromDB.size();i++){
					addEdges("endNode", fromDB.get(i));
				}
			}
			
		} catch (SQLException e) {
			server.LOGG("CONTROLLER/create: CATCH = " + e);
		}
		
		
		
	}

	private void addEdges(String nID, String connected) {
		int x1, x2, y1, y2;
		String coor1 = map.get(nID);
		String coor2 = map.get(connected);
		String[] splitCoor = coor1.split("\\.");
		String[] splitCoor2 = coor2.split("\\.");
	    x1 = Integer.parseInt(splitCoor[0]);
	    y1 = Integer.parseInt(splitCoor[1]);
	    x2 = Integer.parseInt(splitCoor2[0]);
	    y2 = Integer.parseInt(splitCoor2[1]);
	    mp.addEdge(x1, y1, x2, y2);
	}

	private void addNodes(HashMap<String,String> map) {
		int nmbNodes = map.size();

		for(Entry<String, String> entry : map.entrySet()) {
		    String value = entry.getValue();
		    int x,y;
		    String[] splitCoor = value.split("\\.");
		    x = Integer.parseInt(splitCoor[0]);
		    y = Integer.parseInt(splitCoor[1]);
		    mp.addNode(x, y);
		}
		mp.getNodes();
	}

	private void createSQL_GCO(String splitQuery) throws SQLException, JSONException, IOException {
		server.LOGG("CONTROLLER/createSQL_GCO: Inne i metoden createSQL_GCO");
		String msg = "Det saknas en plats som innehåller bokstäverna " + splitQuery + ", försök igen.";
		String GCOQuery = "SELECT place FROM locatormain.places WHERE place LIKE '"+ splitQuery +"%';";
		server.LOGG("CONTROLLER/createSQL_GCO: SQL anropet = " + GCOQuery);
		ArrayList<String> differentPlaces = dbCom.searchComplex(GCOQuery);
		server.LOGG("CONTROLLER/createSQL_GCO: Skapad Arraylist med följande innehåll = " + differentPlaces.toString());
		server.LOGG("CONTROLLER/createSQL_GCO: Arraylisten har storlek = " + differentPlaces.size());
		if (differentPlaces.size() == 0){
			server.LOGG("CONTROLLER/createSQL_GCO: Om Arraylisten inte innegåller något så anropas errorHandler med = " + msg);
			errorHandler(msg);
		} else {
		server.LOGG("CONTROLLER/createSQL_GCO: Om arraylisten är större än 0 så skickas den via createJSON");
		createJSON_GCO(differentPlaces);
		}
	}
	
	private void createSQL_CNF(String splitQuery) throws SQLException, JSONException, IOException {
		server.LOGG("CONTROLLER/createSQL_CNF: Inne i metoden createSQL_CNF");
		String msg = "Välj en plats från listan";
		String CNFQuery = "SELECT count(1) FROM locatormain.places WHERE place = '" + splitQuery + "';";
		server.LOGG("CONTROLLER/createSQL_CNF: SQLanrop = " + CNFQuery);
		boolean finalBool = dbCom.confirmComplex(CNFQuery);
		server.LOGG("CONTROLLER/createSQL_CNF: Boolean som skickas = " + finalBool);
		if (finalBool == false){
			server.LOGG("CONTROLLER/createSQL_CNF: Om boolean är FALSE så skickar vi följande till errorHandler = " + msg );
			errorHandler(msg);
		} 
		server.LOGG("CONTROLLER/createSQL_CNF: Om boolean är TRUE så skickas den till sendBoolToClient = " + finalBool);
		sendBoolToClient(finalBool);

	}

	private String changeDB(String string) throws SQLException, JSONException, IOException {
		server.LOGG("CONTROLLER/changeDB: Inne i metoden changeDB");
		String query = "SELECT dbname FROM locatormain.places WHERE place LIKE '" + string + "';";;
		server.LOGG("CONTROLLER/changeDB: SQLanrop = " + query);
		String newString = dbCom.dBchange(query);
		server.LOGG("CONTROLLER/changeDB: Returnerat från DB = " + newString);
		return newString;
	}
	
	private void createJSON(String[] fromDB) throws IOException, JSONException {
		server.LOGG("CONTROLLER/CreateJSON: Inne i metoden createJSON");
		server.LOGG("CONTROLLER/CreateJSON: Innan det skickas till metoden stringToByte = " + fromDB[1]);
		server.LOGG("CONTROLLER/CreateJSON: Innan det skickas till metoden stringToByte = " + fromDB[4]);
		fromDB[1] = stringToByte(fromDB[1]);
		fromDB[4] = stringToByte(fromDB[4]);

		String jsonText = "{\"name\": \"" + fromDB[0] + "\",\"path\": \"" + fromDB[1] + "\"," + "\"floors\": \""
				+ fromDB[2] + "\",\"id\": \"" + fromDB[3] + "\",\"map\": \"" + fromDB[4] + "\"," + "\"roomid\": \""
				+ fromDB[5] + "\",\"roomCoor\": \"" + fromDB[6] + "\", \"doorCoor\": \"" + fromDB[7] + "\","
				+ "\"corridorCoor\": \"" + fromDB[8] + "\",}";
		server.LOGG("CONTROLLER/CreateJSON: När det gjorts om till JSON");
		JSONObject obj = new JSONObject(jsonText);
		server.LOGG("CONTROLLER/CreateJSON: Innan det skickas till metoden sendCompleteJSONToClient");
		sendCompleteJSONToClient(obj);

	}
	
	private void createJSON_GCO(ArrayList<String> differentPlaces) throws IOException, JSONException {
		server.LOGG("CONTROLLER/CreateJSON_GCO: Inne i metoden createJSON_GCO");
		String jsonText = "{\"nbrOfPlaces\": \"" + Integer.toString(differentPlaces.size());
		String jsonBuildText = "";
		String jsonCloseText = "\",}";
		
		if (differentPlaces.size()>0){
			for (int i = 0; i < differentPlaces.size(); i++){
				jsonBuildText += "\",\"place" + (i+1) + "\": \"" + differentPlaces.get(i);
			}
			jsonBuildText += jsonCloseText;
			jsonText += jsonBuildText;
		} else{
			jsonText += jsonCloseText;
		}
		server.LOGG("CONTROLLER/CreateJSON_GCO: JSON som skickas = " + jsonText);
		JSONObject obj = new JSONObject(jsonText);
		server.LOGG("CONTROLLER/CreateJSON_GCO: Efter det är ett JSONobjekt = " + obj.toString());
		server.LOGG("CONTROLLER/CreateJSON_GCO: Skickas till sendCompleteJSONToClient");
		sendCompleteJSONToClient(obj);

	}
	
	private String stringToByte(String pic) throws IOException {
		server.LOGG("CONTROLLER/StrinToByte: Inne i metoden StrinToByte");
		File fnew = new File(pic);
		Encoder en = Base64.getEncoder();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedImage originalImage = ImageIO.read(fnew);
		System.out.println("Bild skickad.");
		ImageIO.write(originalImage, "BMP", baos);
		byte[] buffer = baos.toByteArray();
		String byteToString = en.encodeToString(buffer);
		server.LOGG("CONTROLLER/StrinToByte: Efter det är encodat.");
		baos.flush();
		baos.close();
		server.LOGG("CONTROLLER/StrinToByte: När det skickas");
		return byteToString;

	}

	private void sendCompleteJSONToClient(JSONObject jsonCool) {
		server.LOGG("CONTROLLER/sendCompleteJSONToClient: Inne i metoden");
		server.sendJsonToClient(jsonCool);

	}

	private void sendBoolToClient(boolean b) {
		server.LOGG("CONTROLLER/sendBoolToClient: Inne i metoden ");
		server.sendBool(b);
	}

	private void errorHandler(String msg) throws JSONException {
		server.LOGG("CONTROLLER/ErrorHandler: Inne i metoden");
		String[] error = new String[2];
		server.LOGG("CONTROLLER/ErrorHandler: Storlek på strängarrayen = " + error.length);
		error[0] = "Error";
		error[1] = msg;
		
		String jsonText = "{\"name\": \"" + error[0] + "\",\"message\": \"" + error[1] + "\",}";
		server.LOGG("CONTROLLER/ErrorHandler: JSON texten = " + jsonText);
		JSONObject obj = new JSONObject(jsonText);
		server.LOGG("CONTROLLER/ErrorHandler: När det skickas.");
		sendCompleteJSONToClient(obj);
		
	}
	
	public void loggDB(String string){
		server.LOGG(string);
	}
	
}

/*
 * 
 * Skapa en metod som skapar en startnod.
 * Skapa en metod som skapar en slutnod.
 * Skapa EDGE-hashmap
 * 
 * 
 */
