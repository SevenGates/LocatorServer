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
	private String dbURL;
	final String username = "root";
	final String password = "k5!298E45H";
	private ServerController controller;
	private HashMap<String,String> map = new HashMap<String, String>();

	
	public DBCommunicator(ServerController serverController) {
		dbURL = "jdbc:mysql://localhost:3306/locatormah?useSSL=false";
		controller = serverController;
	}

	public String[] dBSearchRoom(String roomSearch) throws SQLException, JSONException, IOException {
		controller.loggDB("DBCommunicator/dcSearchRoom: Inne i metoden.");
		Statement stmt = null;
		Connection con = null;
		String dbURL = this.dbURL;
		String query = roomSearch;
		controller.loggDB("DBCommunicator/dcSearchRoom: Anropet till DB = " + roomSearch);
		String[] array = new String[11];
		String[] errorArray = new String[2];
		array[0] = "Error";

		try {
			con = DriverManager.getConnection(dbURL, username, password);
			stmt = (Statement) con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
					
			while (rs.next()) {
				
				array[0] = rs.getString("name");
				System.out.println("");
				System.out.println("--------SÖKNING PÅ RUM---------");
				System.out.println("Byggnad = " + array[0]);
				array[1] = rs.getString("nbrOfpaths");
				System.out.println("Antal paths = " + array[1]);
				array[2] = rs.getString("floors");
				System.out.println("Antal våningar = " + array[2]);
				array[3] = rs.getString("id");
				System.out.println("VåningsId = " + array[3]);
				array[4] = rs.getString("map");
				System.out.println("Våningsplansbild = " +array[4]);
				array[5] = rs.getString("roomid");
				System.out.println("Sökt rum = " +array[5]);
				array[6] = rs.getString("roomCoor");
				System.out.println("Koordinater till rum = " +array[6]);
				array[7] = rs.getString("doorCoor");
				System.out.println("Koordinater till dörr = " +array[7]);
				array[8] = rs.getString("corridorCoor");
				System.out.println("Korridinater till korridor = " +array[8]);
				array[9] = rs.getString("longi");
				System.out.println("Longitud = " +array[9]);
				array[10] = rs.getString("lati");
				System.out.println("Latitud = " +array[10]);
				System.out.println("--------SÖKNING AVSLUTAD---------");
				System.out.println("");
				
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
		dbURL = "jdbc:mysql://localhost:3306/" +result+ "?useSSL=false";
		return result;
	}

	public ArrayList<String> searchComplex(String string) throws SQLException, JSONException, IOException {
		controller.loggDB("DBCommunicator/searchComplex: Inne i metoden");
		Statement stmt = null;
		Connection con = null;
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
		String query = string;
		controller.loggDB("DBCommunicator/confirmComplex: SQLanrop = " + string);
		
		int whatIsThis = -1;
		boolean result = false;
		
		try {
			con = DriverManager.getConnection(dbURL, username, password);
			stmt = (Statement) con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				whatIsThis = rs.getInt("count(1)");
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
		
		if (whatIsThis == 1){
			result = true;
		} else if (whatIsThis == 0){
			result = false;
		}
		return result;
	}

	public HashMap<String,String> dBGetNodes(String queryGetNodes) throws SQLException {
		controller.loggDB("DBCommunicator/dcSearchRoom: Inne i metoden.");
		Statement stmt = null;
		Connection con = null;
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
	
	public ArrayList<String> dBSearchLastNode(String queryGetEdges) throws SQLException {
		Statement stmt = null;
		Connection con = null;
		String query = queryGetEdges;
		ArrayList<String> result = new ArrayList<String>();
		
		try {
			con = DriverManager.getConnection(dbURL, username, password);
			stmt = (Statement) con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				String temp = rs.getString("nID");
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
	
	public String dBGetStartNode(String queryGetStartNode) throws SQLException {
		controller.loggDB("DBCommunicator/dcSearchRoom: Inne i metoden.");
		Statement stmt = null;
		Connection con = null;
		String query = queryGetStartNode;
		String coor = "Najs!";

		try {
			con = DriverManager.getConnection(dbURL, username, password);
			stmt = (Statement) con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
					
			while (rs.next()) {
				coor = rs.getString(1);
				System.out.println("Startnod X/Y = " + coor);
				}
		} catch (SQLException e) {
			controller.loggDB("DBCommunicator/dBGetStartNode: CATCH SQL = " + e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		controller.loggDB("DBCommunicator/dBgetStartNode: Returnerar startnod");
		return coor;
	}

	public String[] getPathName(String queryGetPathName) throws SQLException {
		controller.loggDB("DBCommunicator/dcSearchRoom: Inne i metoden.");
		Statement stmt = null;
		Connection con = null;
		String query = queryGetPathName;
		String[] pathName = new String[7];

		try {
			con = DriverManager.getConnection(dbURL, username, password);
			stmt = (Statement) con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
					
			while (rs.next()) {
				pathName[0] = rs.getString("path1Name");
				System.out.println("S1name = " + pathName[0]);
				pathName[1] = rs.getString("path2Name");
				System.out.println("S2name = " + pathName[1]);
				pathName[2] = rs.getString("path3Name");
				System.out.println("S3name = " + pathName[2]);
				pathName[3] = rs.getString("path4Name");
				System.out.println("S4name = " + pathName[3]);
				pathName[4] = rs.getString("path5Name");
				System.out.println("S5name = " + pathName[4]);
				pathName[5] = rs.getString("path6Name");
				System.out.println("S6name = " + pathName[5]);
				pathName[6] = rs.getString("path7Name");
				System.out.println("S7name = " + pathName[6]);
				}
		} catch (SQLException e) {
			controller.loggDB("DBCommunicator/dBGetStartNode: CATCH SQL = " + e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		controller.loggDB("DBCommunicator/dBgetStartNode: Returnerar startnod");
		return pathName;
	}

}
