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
import java.util.List;
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
	ArrayList<String> lastNode = new ArrayList<String>();

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
//		if (splitQuery.length < 2){
//			return 0;
//		}
		if (splitQuery[0].equals("GCO")) {
			createSQL_GCO(splitQuery[1]);
			return 1;
		} else if (splitQuery[0].equals("CNF")) {
			createSQL_CNF(splitQuery[1]);
			return 2;
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
		server.LOGG("CONTROLLER/createSQL_SEP: ");
		// RÖR INTE I SPRINT 2. 8=====D

	}

	private void createSQL_SER(String[] splitQuery) throws SQLException, JSONException, IOException {
		String msg = "Fel när du angav rummets namn, försök igen.";
		splitQuery[2] = changeDB(splitQuery[2]);
		server.LOGG("CONTROLLER/createSQL_SER: Har ändrat DB med följande information = " + splitQuery[2]);
		
		String SERQuery = "SELECT name, path, floors, id, map, roomid, roomCoor, doorCoor, corridorCoor " + "FROM "
				+ splitQuery[2] + ".building " + "JOIN levels " + "ON building.name=levels.building " + "JOIN room "
				+ "ON levels.id = room.levels " + "WHERE roomid = '" + splitQuery[1] + "'";
		server.LOGG("CONTROLLER/createSQL_SER: SQLQueryn som är genererad = " + SERQuery);
		String[] fromDB ;
		try {
			if (splitQuery[2] == null){
				server.LOGG("CONTROLLER/createSQL_SER: Om fel - Då skickas följande meddelande = " + msg);
				errorHandler(msg);
			} else {
				fromDB = dbCom.dBSearchRoom(SERQuery);
				if (fromDB[0].equals("Error")){
					server.LOGG("CONTROLLER/createSQL_SER: Om felsökt rum - Då skickas följande meddelande = " + msg);
					errorHandler(msg);
				} else {
					server.LOGG("CONTROLLER/createSQL_SER: Else om fromDB är större än 2 platser. Då skickas till createJSON = ");
					createSQL_nodes(splitQuery[1], splitQuery[2], fromDB);
				}
				
				
			}
		} catch (SQLException | JSONException | IOException e) {
			// TODO Auto-generated catch block
			server.LOGG("CONTROLLER/createSQL_SER: CATCH = " + e);
		}
		
		

	}

	private void createSQL_nodes(String room, String dB, String[] fromDB) throws SQLException, JSONException, IOException {
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
		addNodes(map);
		
		for(Entry<String, String> entry : map.entrySet()) {
		    String nID = entry.getKey();
		    createSQL_edge(dB, nID, room);
		}
		
		int x, y;
		String lastNodeID = lastNode.get(0);
		String lastNodeString = map.get(lastNodeID);
		String[] coor = lastNodeString.split("\\.");
		x = Integer.parseInt(coor[0]);
		y = Integer.parseInt(coor[1]);
		
		String splited = floor.substring(2);

		int floorInt = Integer.parseInt(splited); 
		int sx = 0, sy = 0;
		switch (floorInt) {
		case 1 : 
			sx = 1000;
			sy = 581;
			break;
		case 2 : 
			sx = 618;
			sy = 603;
			break;
		case 3 :
			sx = 735;
			sy = 758;
			break;
		case 4 : 
			sx = 714;
			sy = 755;
			break;
		case 5 : 
			sx = 627;
			sy = 830;
			break;
		case 6 : 
			sx = 709;
			sy = 759;
			break;
		}
		
		List <String> listCoords = mp.findShortestPath(sx, sy, x, y);
		
//		int sizeOnArray = fromDB.length;
//		int sizeListCoords = listCoords.size();
//		int totalSize = sizeOnArray+sizeListCoords;
//		String[] newArray = new String[totalSize+1];
//		
//		
//		for(int i = 0; i < fromDB.length; i++){
//			newArray[i] = fromDB[i];
//		}
//		
//		newArray[sizeOnArray] = Integer.toString(sizeListCoords);
//				
//		for (int j = sizeOnArray+1; j < newArray.length; j++){
//			newArray[j] = listCoords.get(j-(sizeOnArray+1));
//		}
		
		int sizeOnArray = fromDB.length;
		int sizeListCoords = listCoords.size();
		String[] newArray = new String[sizeOnArray+1];
		String[] newArray2 = new String[sizeListCoords];
		
		
		for(int i = 0; i < fromDB.length; i++){
			newArray[i] = fromDB[i];
		}
		
		newArray[sizeOnArray] = Integer.toString(sizeListCoords);
				
		for (int j = 0; j < newArray2.length; j++){
			newArray2[j] = listCoords.get(j);
		}
		
		createJSON(newArray, newArray2);
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
		
		try {
			if (nID != null){
			lastNode = dbCom.dBSearchLastNode(queryGetEdges2);
			} else {
				System.out.println("Inte skapat någon LastNode eftersom det är fel i sökningen.");
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
	}

	private void createSQL_GCO(String splitQuery) throws SQLException, JSONException, IOException {
		String msg = "Det saknas en plats som innehåller bokstäverna " + splitQuery + ", försök igen.";
		String GCOQuery = "SELECT place FROM locatormain.places WHERE place LIKE '"+ splitQuery +"%';";
		server.LOGG("CONTROLLER/createSQL_GCO: SQL anropet = " + GCOQuery);
		ArrayList<String> differentPlaces = dbCom.searchComplex(GCOQuery);
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
		String query = "SELECT dbname FROM locatormain.places WHERE place LIKE '" + string + "';";
		String newString = dbCom.dBchange(query);
		server.LOGG("CONTROLLER/changeDB: Returnerat från DB = " + newString);
		return newString;
	}
	
	/*
	 * Metoden som tar emot allt från databasen. Vilken sal, byggnad, våning, bild och hur många noder den har ansluten till sig. 
	 * Sedan gör vi om det till JSON för att skicka till klienten. 
	 */
	
	private void createJSON(String[] newArray, String[] newArray2) throws IOException, JSONException {
		newArray[1] = stringToByte(newArray[1]);
		newArray[4] = stringToByte(newArray[4]);
		String jsonNode = "\"nbrOfNodes\": \"" + newArray[9];
		String jsonBuildText = "";
		String jsonCloseText = "\",}";
		int where = Integer.parseInt(newArray[9]);
		
		for (int i = 0; i < newArray2.length; i++){
			jsonBuildText += "\",\"node" + (i+1) + "\": \"" + newArray2[i];
		}
		
		jsonBuildText += jsonCloseText;
		jsonNode += jsonBuildText;
		System.out.println("-------NODER--------");
		System.out.println("JSONnode = " + jsonNode);

		String jsonText = "{\"name\": \"" + newArray[0] + "\",\"path\": \"" + newArray[1] + "\"," + "\"floors\": \""
				+ newArray[2] + "\",\"id\": \"" + newArray[3] + "\",\"map\": \"" + newArray[4] + "\"," + "\"roomid\": \""
				+ newArray[5] + "\",\"roomCoor\": \"" + newArray[6] + "\", \"doorCoor\": \"" + newArray[7] + "\","
				+ "\"corridorCoor\": \"" + newArray[8] + "\","+jsonNode;
		server.LOGG("CONTROLLER/CreateJSON: När det gjorts om till JSON");
		JSONObject obj = new JSONObject(jsonText);

		System.out.println("Antal noder = " + obj.get("nbrOfNodes"));
		System.out.println("-------NODHANTERING SLUT-------");
		System.out.println("");
		
		sendCompleteJSONToClient(obj);
		

	}
	
	private void createJSON_GCO(ArrayList<String> differentPlaces) throws IOException, JSONException {
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
		sendCompleteJSONToClient(obj);

	}
	
	private String stringToByte(String pic) throws IOException {
		File fnew = new File(pic);
		Encoder en = Base64.getEncoder();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedImage originalImage = ImageIO.read(fnew);
		ImageIO.write(originalImage, "BMP", baos);
		byte[] buffer = baos.toByteArray();
		String byteToString = en.encodeToString(buffer);
		baos.flush();
		baos.close();
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
		String[] error = new String[2];
		error[0] = "Error";
		error[1] = msg;
		
		String jsonText = "{\"name\": \"" + error[0] + "\",\"message\": \"" + error[1] + "\",}";
		server.LOGG("CONTROLLER/ErrorHandler: JSON texten = " + jsonText);
		JSONObject obj = new JSONObject(jsonText);
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
