package server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

public class Server {

	ServerSocket serverSocket;
	public Server() {
		try {
			System.out.println("Waiting...");
			serverSocket = new ServerSocket(8080);
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

	            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
	            output.flush();
	            DataInputStream input = new DataInputStream(clientSocket.getInputStream());

	            while (!interrupted()) {
	               request = input.readUTF();
	               System.out.println(request);
	               ByteArrayOutputStream baos=new ByteArrayOutputStream();
	               BufferedImage img=ImageIO.read(new File("D:/Lite skit/Bilder/Random/locator.bmp"));
	               ImageIO.write(img, "bmp", baos);
	               output.write(baos.toByteArray());
	               output.flush();
	            }
	            clientSocket.close();
	            System.out.println("Disconnected from client");
	        } catch (IOException e) {
	            System.out.println("Error " + e.toString());
	        }

	    }
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args){
		Server server = new Server();
	}
}
