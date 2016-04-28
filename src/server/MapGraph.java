package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import brite.Graph.*;

public class MapGraph {
	
	Graph graph;

	public MapGraph() {
		graph = new Graph();
	}
	
	public void addNode(int x, int y) {
		Node node = new Node();
		NodeConf nodeConf = new  NodeConf(x, y, 0);
		node.setNodeConf(nodeConf);
		graph.addNode(node);
	}
	
	public void getNodes(){
		HashMap map = graph.getNodes();
		Node nodeCheck;
		for(int i = 0; i < Node.getNodeCount(); i++) {
			nodeCheck = (Node)map.get(i);
			System.out.print("Nod" + nodeCheck.getID() +" : " + nodeCheck.getNodeConf().getX()+", ");
			System.out.println(nodeCheck.getNodeConf().getY());
		}
	}
	
	public void addEdge(int x1, int y1, int x2, int y2) {
		HashMap map = graph.getNodes();
		Node nodeCheck, node1 = new Node(-1), node2 = new Node(-1);
		for(int i = 0; i < Node.getNodeCount(); i++) {
			nodeCheck = (Node)map.get(i);
			if(nodeCheck.getNodeConf().getX() == x1 && nodeCheck.getNodeConf().getY() == y1)
				node1 = nodeCheck;
			else if (nodeCheck.getNodeConf().getX() == x2 && nodeCheck.getNodeConf().getY() == y2)
				node2 = nodeCheck;
		}
		
		if(node1.getID() != -1 && node2.getID() != -1) {
			Edge edge = new Edge(node1,node2);
			edge.setDirection(GraphConstants.UNDIRECTED);
			edge.setEuclideanDist(0);
			graph.addEdge(edge);
			
		}
	}
	
	public void getEdge(){
		HashMap map = graph.getEdges();
		System.out.println("Antal edges i hashmapen = " + map.size());
		Edge edgeCheck = (Edge) map.get(655371);
		System.out.println("EdgeID = " + edgeCheck.getID());
		System.out.println("Vafan händer här? " + edgeCheck.computeID(11, 12));
	}
	
	public void addEdge (int id1, int id2) {
		Node node1, node2;
		node1=graph.getNodeFromID(id1);
		node2=graph.getNodeFromID(id2);
	
		Edge edge = new Edge(node1,node2);
		edge.setDirection(GraphConstants.UNDIRECTED);
		edge.setEuclideanDist(0);
		graph.addEdge(edge);
	}
	
	public int getNodeId(int x, int y) {
		HashMap map = graph.getNodes();
	    Node nodeCheck, node1 = new Node(-1);
	    for(int i = 0; i < Node.getNodeCount(); i++) {
	        nodeCheck = (Node)map.get(i);
	        if(nodeCheck.getNodeConf().getX() == x && nodeCheck.getNodeConf().getY() == y)
	            node1 = nodeCheck;
	    }
	    return node1.getID();
	}


	public int getEdgeId(int x1, int y1, int x2, int y2) {
		int node1 = getNodeId(x1, y1), node2 = getNodeId(x2,y2);
	  	return Edge.computeID(node1,node2);
	}
	
	public List<String> findShortestPath(int x1, int y1, int x2, int y2) {
		HashMap map = graph.getNodes();
		Node nodeCheck, node1 = new Node(-1), node2 = new Node(-1);
		for(int i = 0; i < Node.getNodeCount(); i++) {
			nodeCheck = (Node)map.get(i);
			if(nodeCheck.getNodeConf().getX() == x1 && nodeCheck.getNodeConf().getY() == y1)
				node1 = nodeCheck;
			else if (nodeCheck.getNodeConf().getX() == x2 && nodeCheck.getNodeConf().getY() == y2)
				node2 = nodeCheck;
		}
		
		if(node1.getID() != -1 && node2.getID() != -1) {
			return findShortestPath(node1.getID(),node2.getID());
		}
		return null;
	}
	
	public List<String> findShortestPath (int start, int end){
		Dijkstra algo = new Dijkstra();
		List<String> shortestPath = new ArrayList<String>();
		HashMap map = algo.runDijkstra(graph, graph.getNodeFromID(start));
		
		Node nextNode;
		Node currentNode=graph.getNodeFromID(end);
		NodeConf nodeConf = currentNode.getNodeConf();
		shortestPath.add(0, nodeConf.getX() + "." + nodeConf.getY());
		while (currentNode.getID()!=start) {
			nextNode=(Node)map.get(currentNode);
			nodeConf = nextNode.getNodeConf();
			shortestPath.add(0, nodeConf.getX() + "." + nodeConf.getY());
			currentNode=nextNode;
		}
		return shortestPath;
	}
	
	/**
	 * Testfunktion med exempel kod för användning.
	 */
//	public static void main(String[] args) {
//		MapGraph graph = new MapGraph();
//		graph.addNode(0, 1);
//		graph.addNode(0, 2);
//		graph.addNode(0, 3);
//		graph.addNode(0, 4);
//		graph.addNode(0, 5);
//		graph.addNode(1, 1);
//		graph.addNode(1, 2);
//		graph.addNode(1, 3);
//		graph.addNode(1, 4);
//		graph.addNode(1, 5);
//		graph.addEdge(0, 1);
//		graph.addEdge(1, 2);
//		graph.addEdge(2, 3);
//		graph.addEdge(3, 4);
//		graph.addEdge(0, 5);
//		graph.addEdge(5, 6);
//		graph.addEdge(6, 7);
//		graph.addEdge(7, 8);
//		graph.addEdge(8, 9);
//		graph.addEdge(9, 4);
//		List <String> listCoords = graph.findShortestPath(0, 1, 0, 5);
//		List <String> listID = graph.findShortestPath(0, 8);
//		
//		for (String S : listCoords){
//			System.out.println(S);
//		}
//		System.out.println("-----");
//		for (String S : listID){
//			System.out.println(S);
//		}
//	}
}
