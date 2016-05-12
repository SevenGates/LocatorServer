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

import brite.Graph.Node;

public class ServerController implements Serializable{
	private static final long serialVersionUID = 2797313745946203668L;
	Server server;
	DBCommunicator dbCom;
	MapGraph mp = new MapGraph();
	int x, y, s1x, s1y, s2x, s2y, s3x, s3y, s4x, s4y;
	private HashMap<String,String> map = new HashMap<String, String>();
	ArrayList<String> lastNode = new ArrayList<String>();
	ArrayList<String> fromDBNew = new ArrayList<String>();
	String forNow;

	public ServerController(Server server) {
		this.server = server;
		dbCom = new DBCommunicator(this);
	}

	public int msgFromClient(String request) throws SQLException, JSONException, IOException {
		String sqlQuery = request;
		server.LOGG("CONTROLLER/msgFromClient: Kontrollerar vad controllern tar emot från klienten = " + request);
		String[] splitQuery = sqlQuery.split(",");


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
		String SERQuery = "SELECT name, path, floors, id, map, roomid, roomCoor, doorCoor, corridorCoor, nbrOfPaths, longi, lati " + "FROM "
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
			server.LOGG("CONTROLLER/createSQL_SER: CATCH = " + e);
		}
		
		

	}

	private void createSQL_nodes(String room, String dB, String[] fromDB) throws SQLException, JSONException, IOException {
		String floor = createSQL_getFloor(dB, room);
		String queryGetNodes = "SELECT nID, coor " + "FROM "
				+ dB + ".node " + "WHERE nID LIKE '" + floor + "%'";
		map = dbCom.dBGetNodes(queryGetNodes);
		addNodes(map);
		
		for(Entry<String, String> entry : map.entrySet()) {
		    String nID = entry.getKey();
		    createSQL_edge(dB, nID, room);
		}
		getTheLastNode(dB, room);
		getTheStartNode(floor);
		ArrayList<String> all = findShortestPath(fromDB);
		createJSON(all);
		
	}
	
	private void getTheLastNode(String dB, String room) {
		String queryGetEdges2 = "SELECT nID " + "FROM "
				+ dB + ".noderoom " + "WHERE roomID = '" + room + "';";
		try {
			lastNode = dbCom.dBSearchLastNode(queryGetEdges2);
		} catch (SQLException e) {
			server.LOGG("CONTROLLER/create: CATCH = " + e);
		}	
		String lastNodeID = lastNode.get(0);
		String lastNodeString = map.get(lastNodeID);
		String[] coor = lastNodeString.split("\\.");
		x = Integer.parseInt(coor[0]);
		y = Integer.parseInt(coor[1]);
	}

	private ArrayList findShortestPath(String[] fromDB) {
		List <String> listCoords = mp.findShortestPath(s1x, s1y, x, y);
		ArrayList<String> findShortestPath = new ArrayList<String>();
		for (int k = 0; k < fromDB.length; k++){ findShortestPath.add(fromDB[k]);}
		findShortestPath.add(Integer.toString(listCoords.size()));	
		for (int j = 0; j < listCoords.size(); j++){ findShortestPath.add(listCoords.get(j));}
		List <String> listCoords2 = mp.findShortestPath(s2x, s2y, x, y);
		findShortestPath.add(Integer.toString(listCoords2.size()));	
		for (int i = 0; i < listCoords2.size(); i++){ findShortestPath.add(listCoords2.get(i));}
		List <String> listCoords3 = mp.findShortestPath(s3x, s3y, x, y);
		findShortestPath.add(Integer.toString(listCoords3.size()));	
		for (int c = 0; c < listCoords3.size(); c++){ findShortestPath.add(listCoords3.get(c));}
		map.clear();
		System.out.println(findShortestPath.toString());
		return findShortestPath;
	}

	private void getTheStartNode(String floor) {
		String splited = floor.substring(2);
		if (splited.equals("E")){splited = "1";}
		int floorInt = Integer.parseInt(splited); 
		
		switch (floorInt) {
		case 1 : 
			s1x = 1000; // Vanligaste ingången
			s1y = 581;
			s2x = 861; // Mot vägen eller G8
			s2y = 705;
			s3x = 800; // Mot kanalen
			s3y = 493;
			s4x = 0;
			s4y = 0;
			break;
		case 2 : 
			s1x = 618; // Trappa
			s1y = 603;
			s2x = 690; // Hiss A
			s2y = 767;
			s3x = 924; // Hiss B
			s3y = 724;
			s4x = 0;
			s4y = 0;
			break;
		case 3 :
			s1x = 735; // Hiss A och Trappa
			s1y = 758;
			s2x = 986; // Hiss B 
			s2y = 709;
			s3x = 915; // Hiss C
			s3y = 418;
			s4x = 0;
			s4y = 0;
			break;
		case 4 : 
			s1x = 714; // Hiss A och Trappa
			s1y = 755;
			s2x = 714; // Hiss A och Trappa
			s2y = 755;
			s3x = 714; // Hiss A och Trappa
			s3y = 755;
//			s2x = 976; // Hiss B 
//			s2y = 703;
//			s3x = 900; // Hiss C
//			s3y = 410;
//			s4x = 0;
//			s4y = 0;
			break;
		case 5 : 
			s1x = 627; // Hiss A och Trappa
			s1y = 830;
			s2x = 885; // Hiss B 
			s2y = 786;
			s3x = 810; // Hiss C
			s3y = 485;
			s4x = 0;
			s4y = 0;
			break;
		case 6 : 
			s1x = 709; // Hiss A och Trappa
			s1y = 759;
			s2x = 978; // Hiss B 
			s2y = 712;
			s3x = 900; // Hiss C
			s3y = 410;
			s4x = 0;
			s4y = 0;
			break;
		}
		
	}

	private String createSQL_getFloor(String dB, String room) {
		String floor = "";
		String queryGetFloor = "SELECT levels " + "FROM "
				+ dB + ".room " + "WHERE roomid = '" + room + "'";
		
		try {
			floor = dbCom.dBSearchFloor(queryGetFloor);
						
		} catch (SQLException | JSONException | IOException e) {
			server.LOGG("CONTROLLER/createSQL_nodes: CATCH = " + e);
		}
		return floor;
	}

	private void createSQL_edge(String dB, String nID, String room) throws SQLException, JSONException, IOException {
		String queryGetEdges = "SELECT connectID " + "FROM "
				+ dB + ".edge " + "WHERE nID = '" + nID + "';";
		try {
			int test = 0;
			fromDBNew = dbCom.dBSearchEdges(queryGetEdges);
			if (fromDBNew.size() > 0){
				for (int i = 0; i < fromDBNew.size();i++){
					addEdges(nID, fromDBNew.get(i));
					
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
	
	private void createJSON(ArrayList<String> all) throws IOException, JSONException {
		/*
		 * Metoden som tar emot allt från databasen. Vilken sal, byggnad, våning, bild och hur många noder den har ansluten till sig. 
		 * Sedan gör vi om det till JSON för att skicka till klienten. 
		 */
		
		String pic2 = stringToByte(all.get(4));
		String jsonBuildText = "";
		String jsonBuildText2 = "";
		String jsonBuildText3 = "";
		String jsonCloseText = "\",}";
		String longi = "55.6091855";
		String lati = "12.9925708";
		int inArray = 12+Integer.parseInt(all.get(11));
		int inArray2 = (inArray + Integer.parseInt(all.get(inArray)) + 1);
		
		
		for (int i = 0; i < Integer.parseInt(all.get(11)); i++){
			jsonBuildText += "\",\"s1node" + (i+1) + "\": \"" + all.get(11+(i+1));
		}
						
		for (int i = 0; i < Integer.parseInt(all.get(inArray)); i++){
			jsonBuildText2 += "\",\"s2node" + (i+1) + "\": \"" + all.get(inArray+(i+1));
		}
				
		for (int i = 0; i < Integer.parseInt(all.get(inArray2)); i++){
			jsonBuildText3 += "\",\"s3node" + (i+1) + "\": \"" + all.get(inArray2+(i+1));
		}
		
		if (all.get(3).equals("NIE")){
			all.set(3, "NI1");
		}
		
		System.out.println(all.get(3));
				
		String jsonText = "{\"name\": \"" + all.get(0) + "\","+ 
							"\"nbrOfPaths\": \"" + all.get(1) + "\"," + 
							"\"floors\": \""+ all.get(2) + "\","+ 
							"\"id\": \"" + all.get(3) + "\","+
							"\"map\": \"" + pic2 + "\"," + 
							"\"roomid\": \"" + all.get(5) + "\","+
							"\"roomCoor\": \"" + all.get(6) + "\","+ 
							"\"doorCoor\": \"" + all.get(7) + "\","	+ 
							"\"corridorCoor\": \"" + all.get(8) + "\","+
							"\"nbrOfNodesS1\": \"" + all.get(11)+
							jsonBuildText+"\","+
							"\"nbrOfNodesS2\": \"" + all.get(inArray)+ 
							jsonBuildText2+"\","+
							"\"nbrOfNodesS3\": \"" + all.get(inArray2)+ 
							jsonBuildText3+"\","+
							"\"long\": \"" + all.get(9) + "\","+ 
							"\"lat\": \"" + all.get(10) + jsonCloseText;
		
		server.LOGG("CONTROLLER/CreateJSON: När det gjorts om till JSON");
		JSONObject obj = new JSONObject(jsonText);
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
