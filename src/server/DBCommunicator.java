package server;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Base64.Encoder;
import javax.imageio.ImageIO;
import org.json.JSONException;
import org.json.JSONObject;
import com.mysql.jdbc.Statement;

public class DBCommunicator implements Serializable{
	private ServerController controller;
	private HashMap<String,String> map = new HashMap<String, String>();

	
	public DBCommunicator(ServerController serverController) {
		controller = serverController;
	}

	public String[] dBSearchRoom(String roomSearch) throws SQLException, JSONException, IOException {
		controller.loggDB("DBCommunicator/dcSearchRoom: Inne i metoden.");
		Statement stmt = null;
		Connection con = null;
		String dbURL = "jdbc:mysql://localhost:3306/locatormah?useSSL=false";
		String username = "root";
		String password = "k5!298E45H";
		String query = roomSearch;
		controller.loggDB("DBCommunicator/dcSearchRoom: Anropet till DB = " + roomSearch);
		String[] array = new String[9];
		String[] errorArray = new String[2];

		try {
			con = DriverManager.getConnection(dbURL, username, password);
			stmt = (Statement) con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
					
			while (rs.next()) {
				if (rs.getString("name") == null){
					controller.loggDB("DBCommunicator/dcSearchRoom: I TRY/WHILE/IF, om name = null");
					return errorArray;
				} else{
				array[0] = rs.getString("name");
				array[1] = rs.getString("path");
				array[2] = rs.getString("floors");
				array[3] = rs.getString("id");
				array[4] = rs.getString("map");
				array[5] = rs.getString("roomid");
				array[6] = rs.getString("roomCoor");
				array[7] = rs.getString("doorCoor");
				array[8] = rs.getString("corridorCoor");
				}
			}
		} catch (SQLException e) {
			controller.loggDB("DBCommunicator/dcSearchRoom: CATCH SQL = " + e);
		} finally {
			if (stmt != null) {
				controller.loggDB("DBCommunicator/dcSearchRoom: Om stmt != null så stänget vi stmt");
				stmt.close();
			}
		}
		controller.loggDB("DBCommunicator/dcSearchRoom: Returnerar array");
		return array;
	}

	public String dBSearchFloor(String roomSearch) throws SQLException, JSONException, IOException {
		controller.loggDB("DBCommunicator/dcSearchRoom: Inne i metoden.");
		Statement stmt = null;
		Connection con = null;
		String dbURL = "jdbc:mysql://localhost:3306/locatormah?useSSL=false";
		String username = "root";
		String password = "k5!298E45H";
		String query = roomSearch;
		controller.loggDB("DBCommunicator/dcSearchRoom: Anropet till DB = " + roomSearch);
		String floor = "";

		try {
			con = DriverManager.getConnection(dbURL, username, password);
			stmt = (Statement) con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
					
			while (rs.next()) {
				floor = rs.getString("levels");
				
			}
		} catch (SQLException e) {
			controller.loggDB("DBCommunicator/dcSearchRoom(string): CATCH SQL = " + e);
		} finally {
			if (stmt != null) {
				controller.loggDB("DBCommunicator/dcSearchRoom: Om stmt != null så stänget vi stmt");
				stmt.close();
			}
		}
		controller.loggDB("DBCommunicator/dcSearchRoom: Returnerar array");
		return floor;
	}
	
	public String dBchange(String string) throws SQLException, JSONException, IOException {
		controller.loggDB("DBCommunicator/dBchange: Inne i metoden.");
		Statement stmt = null;
		Connection con = null;
		String dbURL = "jdbc:mysql://localhost:3306/locatormain?useSSL=false";
		String username = "root";
		String password = "k5!298E45H";
		String query = string;
		controller.loggDB("DBCommunicator/dBchange: SQLanropet = " + string);
		String result = null;
		try {
			con = DriverManager.getConnection(dbURL, username, password);
			stmt = (Statement) con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				result = rs.getString("dbname");
			}
		} catch (SQLException e) {
			controller.loggDB("DBCommunicator/dBchange: CATCH SQL = " + e);
		} finally {
			if (stmt != null) {
				controller.loggDB("DBCommunicator/dBchange: Om stmt != null så stänger vid stmt");
				stmt.close();
			}
		}
		
		return result;
	}

	public ArrayList<String> searchComplex(String string) throws SQLException, JSONException, IOException {
		controller.loggDB("DBCommunicator/searchComplex: Inne i metoden");
		Statement stmt = null;
		Connection con = null;
		String dbURL = "jdbc:mysql://localhost:3306/locatormain?useSSL=false";
		String username = "root";
		String password = "k5!298E45H";
		String query = string;
		controller.loggDB("DBCommunicator/searchComplex: SQL anrop = " + string);
		ArrayList<String> result = new ArrayList<String>();
		
		try {
			con = DriverManager.getConnection(dbURL, username, password);
			stmt = (Statement) con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				result.add(rs.getString("place"));
			}
		} catch (SQLException e) {
			controller.loggDB("DBCommunicator/searchComplex: CATCH SQL = " + e);
		} finally {
			if (stmt != null) {
				controller.loggDB("DBCommunicator/searchComplex: Om stmt != null så stänger vi stmt");
				stmt.close();
			}
		}
		controller.loggDB("DBCommunicator/searchComplex: Returnerar resultatet = " + result.toString());
		return result;
	}
	
	public boolean confirmComplex(String string) throws SQLException, JSONException, IOException {
		controller.loggDB("DBCommunicator/confirmComplex: Inne i metoden");
		Statement stmt = null;
		Connection con = null;
		String dbURL = "jdbc:mysql://localhost:3306/locatormain?useSSL=false";
		String username = "root";
		String password = "k5!298E45H";
		String query = string;
		controller.loggDB("DBCommunicator/confirmComplex: SQLanrop = " + string);

		int whatIsThis = -1;
		boolean result = false;
		
		try {
			con = DriverManager.getConnection(dbURL, username, password);
			stmt = (Statement) con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				whatIsThis = rs.getInt("place");
			}
			if (whatIsThis == 1){
				result = true;
			} else if (whatIsThis == 0){
				result = false;
			}
		} catch (SQLException e) {
			controller.loggDB("DBCommunicator/confirmComplex: CATCH SQL = " + e);
		} finally {
			if (stmt != null) {
				controller.loggDB("DBCommunicator/confirmComplex: Om stmt != null så stänger vi stmt");
				stmt.close();
			}
		}
		controller.loggDB("DBCommunicator/confirmComplex: Returnerar = " + result);
		return result;
	}

	public HashMap<String,String> dBGetNodes(String queryGetNodes) throws SQLException {
		controller.loggDB("DBCommunicator/dcSearchRoom: Inne i metoden.");
		Statement stmt = null;
		Connection con = null;
		String dbURL = "jdbc:mysql://localhost:3306/locatormah?useSSL=false";
		String username = "root";
		String password = "k5!298E45H";
		String query = queryGetNodes;

		try {
			con = DriverManager.getConnection(dbURL, username, password);
			stmt = (Statement) con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
					
			while (rs.next()) {
				String key = rs.getString("nID");
				String value = rs.getString("coor");
				map.put(key, value);
				}
		} catch (SQLException e) {
			controller.loggDB("DBCommunicator/dBGetNodes: CATCH SQL = " + e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		controller.loggDB("DBCommunicator/dBgetNodes: Returnerar noder");
		return map;
	}

	public String dBGetEndNode(String queryGetNodes) throws SQLException {
		controller.loggDB("DBCommunicator/dcSearchRoom: Inne i metoden.");
		Statement stmt = null;
		Connection con = null;
		String dbURL = "jdbc:mysql://localhost:3306/locatormah?useSSL=false";
		String username = "root";
		String password = "k5!298E45H";
		String query = queryGetNodes;
		String value = "NAJS";

		try {
			con = DriverManager.getConnection(dbURL, username, password);
			stmt = (Statement) con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
					
			while (rs.next()) {
				value = rs.getString("corridorCoor");
				}
		} catch (SQLException e) {
			controller.loggDB("DBCommunicator/dBGetNodes: CATCH SQL = " + e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		controller.loggDB("DBCommunicator/dBgetNodes: Returnerar noder");
		return value;
	}


	public ArrayList<String> dBSearchEdges(String queryGetEdges) throws SQLException {
		Statement stmt = null;
		Connection con = null;
		String dbURL = "jdbc:mysql://localhost:3306/locatormain?useSSL=false";
		String username = "root";
		String password = "k5!298E45H";
		String query = queryGetEdges;
		ArrayList<String> result = new ArrayList<String>();
		
		try {
			con = DriverManager.getConnection(dbURL, username, password);
			stmt = (Statement) con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				String temp = rs.getString("connectID");
				if(temp!= null) {
					result.add(temp);	
				}
				
			}
		} catch (SQLException e) {
			controller.loggDB("DBCommunicator/searchComplex: CATCH SQL = " + e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		controller.loggDB("DBCommunicator/searchComplex: Returnerar resultatet = " + result.toString());
		return result;
	}
}
