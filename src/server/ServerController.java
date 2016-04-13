package server;

public class ServerController {
	Server server;
	DBCommunicator dbCom;
		
	public ServerController(Server server){
		this.server = server;
		dbCom = new DBCommunicator();
	}
	
	public void fromServer(String request) {
		// Gör om till SQL anrop. Och skickar till DB.
		String sqlQuery = request; // Här ska det göras om till anrop. 
		String[] fromDB = dbCom.query(sqlQuery); //resultat från DB efter anrop. 
		
	}

}
