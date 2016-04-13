package server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class Server {
	private String filename;
	ServerSocket serverSocket;
	ServerController controller;
	
	public Server() {
		try {
			System.out.println("Waiting...");
			serverSocket = new ServerSocket(8080);
			controller = new ServerController(this);
//			MySQLTest sql = new MySQLTest();
//        	sql.main(null);
			while(true) {
				Socket clientSocket = serverSocket.accept();
				Thread t = new ThreadHandler(clientSocket);
				t.start();
			}
		} catch (IOException e) {
			System.out.println("Error:" + e.toString());
		}
		System.out.println("End!");
	}
	
	private class ThreadHandler extends Thread {
	    Socket clientSocket;
	    String request;
	    
	    ThreadHandler(Socket client) {
	    	clientSocket = client;
	    }

	    public void run() {   	
	        try {
	        	
	            OutputStream output = clientSocket.getOutputStream();
	            output.flush();
	            DataInputStream input = new DataInputStream(clientSocket.getInputStream());

	            while (!interrupted()) {
	            	request = input.readUTF();
	            	controller.fromServer(request);
	            	// Här ska det skickas till controllern som kontrollerar vad klienten vill. 
	            	
	            	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            	BufferedImage img=ImageIO.read(new File("bajs"));
	            	ImageIO.write(img, "BMP", baos);
	            	ByteArrayInputStream bi = new ByteArrayInputStream(baos.toByteArray());
	            	
	            	
	            	byte[] buffer = new byte[999999999];
   					int read = bi.read(buffer);
   					output.write(buffer, 0, read);
   					output.flush();
   					output.close();
   					System.out.println(read);
	            }
	            clientSocket.close();
	            System.out.println("Disconnected from client");
	        } catch (IOException e) {
	            System.out.println("Error " + e.toString());
	        }

	    }
	}
	

//	private class MySQLTest {
//		ArrayList<String> test = new ArrayList<String>();
//		
//		public ArrayList<String> getAL(){
//			return test;
//		}
//	
//	public void main(String args[]) {
//		
//		String view = null;
//		String dbURL = "jdbc:mysql://localhost:3306/locatormah?useSSL=false";
//		String username = "root";
//		String password = "k5!298E45H";
//		Connection dbCon = null;
//		Statement stmt = null;
//		ResultSet rs = null;
//		String query = "SELECT * FROM room";
//		try {
//			// getting database connection to MySQL server
//			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//			dbCon = DriverManager.getConnection(dbURL, username, password);
//			// getting PreparedStatment to execute query
//			stmt = dbCon.prepareStatement(query);
//			// Resultset returned by query
//			rs = stmt.executeQuery(query);
//			
//			while (rs.next()) {
//				
//				String name = rs.getString(1);
//				String floors = rs.getString(2);
//				view = rs.getString(3);
//				System.out.print("Byggnad/Våning " + name + ", ");
//				System.out.print("Salsnamn " + floors + ", ");
//				System.out.println("Koordinat " + view);
//
//				test.add(view);
//				
//			}
//			
//		} catch (SQLException ex) {
//			System.out.println(ex.toString());
//			System.out.println("Någor har gått fel.");
//	//		Logger.getLogger(CollectionTest.class.getName()).log(Level.SEVERE, null, ex);
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			System.out.println(e.toString());
//		} finally {
//			// close connection ,stmt and resultset here
//			
//			
//		}
//	
//	}
//	
//}
	
	
	@SuppressWarnings("unused")
	public static void main(String[] args){
		Server server = new Server();
		
	}


	public void getFromController(String[] fromDB) {
		// TODO Auto-generated method stub
		
	}
}
