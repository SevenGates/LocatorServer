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

import org.json.JSONException;
import org.json.JSONObject;

public class Server implements Serializable{
	private String filename;
	ServerSocket serverSocket;
	ServerController controller;
	String completeJson;
	boolean confirmComplex;
	Object send;
	
	public Server() {
		try {
			System.out.println("Waiting...");
			serverSocket = new ServerSocket(8080);
			controller = new ServerController(this);
			
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
	        	
	            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
	            output.flush();
	            DataInputStream input = new DataInputStream(clientSocket.getInputStream());

	            while (!interrupted()) {
	            	request = input.readUTF();
	            	int sendNbr = controller.msgFromClient(request);		            	
	            	switch (sendNbr){
	            	case 1 : 
	            		output.writeObject(completeJson);
		            	output.flush();
		            	output.close();
	            		break;
	            	case 2 : 
	            		output.writeBoolean(confirmComplex);
		            	output.flush();
		            	output.close();
	            		break;
	            	}       	
	            }
	            clientSocket.close();
	            System.out.println("Disconnected from client");
	        } catch (IOException e) {
	            System.out.println("Error " + e.toString());
	        } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	public static void main(String[] args){
		Server server = new Server();
		
	}

	public void sendJsonToClient(JSONObject jsonCool) {
		completeJson = jsonCool.toString();
	}

	
	public void sendBool(boolean b) {
		confirmComplex = b;
	}
}
