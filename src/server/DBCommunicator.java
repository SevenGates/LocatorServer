package server;

public class DBCommunicator {
	

	public String[] query(String sqlQuery) {
		// anropar DB. Får tillbaka data i form av strängar. Som vi lagrar i en sträng array. Och sedan returnerar.
		String[] resultFromDB = new String[11]; // En string array som just nu kan hålla all data från alla tabeller (tillsammans 11st)
		return resultFromDB; 
	}

} 

/*
 * Vid felanrop till DB tycker jag och Isak att vi ska skapa en string[] med info om felkoden som returneras ändå. 
 * Utifrån den kan vi i controllern se vilket fel och skicka en standardbild till klienten och kort info. 
*/