package server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import javax.imageio.ImageIO;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.*;

/**
 * A class for a javaServer. 
 * @author Chrille
 *
 */

public class Server implements Serializable{
	private String filename;
	private int port = 8080;
	ServerSocket serverSocket;
	ServerController controller;
	String completeJson;
	boolean confirmComplex;
	Object send;
	private final static Logger LOGG = Logger.getLogger("ServerLogg");
	
	/*
	 * Constructor that starts the loggfile, serversocket and a instance of the controller.
	 * Starting a thread that looops and waiting for a client to connect. 
	 */
	public Server() {
		try {
			newLoggFile();
			System.out.println("Waiting...");
			LOGG.info("KONSTRUKTORN: Server is waiting for response.\n");
			serverSocket = new ServerSocket(port);
			LOGG.info("KONSTRUKTORN: ServerSocket is created with port : " + port + "\n");
			controller = new ServerController(this);
			LOGG.info("KONSTRUKTORN: Controller is created\n");
			
			while(true) {
				Socket clientSocket = serverSocket.accept();
				LOGG.info("KONSTRUKTORN: Klienten är ansluten\n");
				Thread t = new ThreadHandler(clientSocket);
				LOGG.info("KONSTRUKTORN: ThreadHandler är skapad av klientsocketen. Trådens namn : " + t.getName()+"\n");
				t.start();
				LOGG.info("KONSTRUKTORN: Tråden är startad\n");
			}
		} catch (IOException e) {
			LOGG.warning("KONSTRUKTORN: CATCH IOE: " + e.toString()+"\n");
		}
		LOGG.info("KONSTRUKTORN: End!\n");
	} 
	
	/**
	 * Inner class that handles the threads.
	 * @author Chrille
	 *
	 */
	private class ThreadHandler extends Thread {
	    Socket clientSocket;
	    String request;
	    
	    ThreadHandler(Socket client) {
	    	clientSocket = client;
			LOGG.info("THREADHANDLER: clientSocket tilldelas = " + client.toString()+"\n");
	    }
	    
	    public void run() {   	
	        try {
	        	
	            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
				LOGG.info("THREADHANDLER: objectoutputstream är instansierad\n");
	            output.flush();
	            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
				LOGG.info("THREADHANDLER: Datainputstream är instansierad\n");

	            /*
	             * A while-loop that is reading inputs from the client. 
	             * 
	             */
	            while (!interrupted()) {
	            	request = input.readUTF();
					LOGG.info("THREADHANDLER: Läser innehållet från input = " + request+"\n");

	            	int sendNbr = controller.msgFromClient(request);
					LOGG.info("THREADHANDLER: msgFromClient skickas till Controller med info = " + sendNbr+"\n");
	            	switch (sendNbr){
	            	case 1 : 
	    				LOGG.info("THREADHANDLER: Switch 1: Här skrivs Json object genom output streamen\n");
	            		output.writeObject(completeJson);
	    				LOGG.info("THREADHANDLER: Switch 1: Json skickat genom output\n");
		            	output.flush();
		            	output.close();
	    				LOGG.info("THREADHANDLER: Switch 1: Outputstreamen är flushad och stängd.\n");
	            		break;
	            	case 2 : 
	    				LOGG.info("THREADHANDLER: Switch 2: Här skickas boolean genom output streamen\n");
	            		output.writeBoolean(confirmComplex);
	    				LOGG.info("THREADHANDLER: Switch 2: Boolean är skickat genom outputstreamen = " + confirmComplex+"\n");
		            	output.flush();
		            	output.close();
	    				LOGG.info("THREADHANDLER: Switch 2: Outputstreamen är flushad och stängd.\n");
	            		break;
	            	}       	
	            }
	            clientSocket.close();
				LOGG.info("THREADHANDLER: Clientsocket stängs\n");
	        } catch (IOException e) {
				LOGG.warning("KONSTRUKTORN: CATCH IOE: " + e.toString()+"\n");
	        } catch (SQLException e) {
				LOGG.warning("KONSTRUKTORN: CATCH SQL: " + e.toString()+"\n");
			} catch (JSONException e) {
				LOGG.warning("KONSTRUKTORN: CATCH JSON: " + e.toString()+"\n");

			}
	    }
	}
	
	public static void main(String[] args){
		Server server = new Server();
		
	}

	public void sendJsonToClient(JSONObject jsonCool) {
		LOGG.info("sendJsonToClient: Skickar info till klient\n");
		completeJson = jsonCool.toString();
	}

	public void sendBool(boolean b) {
		LOGG.info("sendJsonToClient: Skickar följande: " + b);
		confirmComplex = b;
	}
	
	private void newLoggFile() throws IOException {
		String filename = "serverLogg_" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
		File f = new File("./loggs/" + filename);
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		FileHandler fh = new FileHandler("./loggs/" + filename + ".txt");
		LOGG.setUseParentHandlers(false);
		LOGG.addHandler(fh);
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);
	}
	
	public void LOGG (String string){
		LOGG.info(string);
	}
}

