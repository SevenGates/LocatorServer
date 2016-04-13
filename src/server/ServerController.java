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
		sendToDB(sqlQuery);
		
		
	}
	
	public void sendToDB(String sqlQuery) {
		String[] fromDB = dbCom.query(sqlQuery); //resultat från DB efter anrop. 
		checkContent(fromDB);
	}

	private void checkContent(String[] fromDB) {
		//kontrollera innehållet 
		if (fromDB[0] == "Error"){
			errorHandler(fromDB[1]);
		} else{
			server.getFromController(fromDB);
		}
		
	}

	private void errorHandler(String fromDB) {
		// Tar emot vilken typ av error. Sedan gör vi något coolt med det.
		
		
	}

}
