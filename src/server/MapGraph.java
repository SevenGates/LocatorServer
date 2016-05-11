package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import brite.Graph.*;

public class MapGraph {
	
	Graph graph;
	int count = 0;

	public MapGraph() {
		graph = new Graph();
	}
	
	public void addNode(int x, int y) {
		Node node = new Node();
		node.setID(count);
		count++;
		NodeConf nodeConf = new  NodeConf(x, y, 0);
		node.setNodeConf(nodeConf);
		graph.addNode(node);
	}
	
	public void getNodes(){
		HashMap map = graph.getNodes();
		Node nodeCheck;
		for(int i = 0; i < Node.getNodeCount()+1; i++) {
			nodeCheck = (Node)map.get(i);
			System.out.print("Nod" + nodeCheck.getID() +" : " + nodeCheck.getNodeConf().getX()+", ");
			System.out.println(nodeCheck.getNodeConf().getY());
		}
	}
	
	public void addEdge(int x1, int y1, int x2, int y2) {
		HashMap map = graph.getNodes();
		Node nodeCheck, node1 = new Node(-1), node2 = new Node(-1);
		for(int i = 0; i < Node.getNodeCount()+1; i++) {
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
	    for(int i = 0; i < Node.getNodeCount()+1; i++) {
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
		
		for(int i = 0; i < Node.getNodeCount()+1; i++) {
			nodeCheck = (Node)map.get(i);
			if(nodeCheck.getNodeConf().getX() == x1 && nodeCheck.getNodeConf().getY() == y1){
				node1 = nodeCheck;
				}
			else if (nodeCheck.getNodeConf().getX() == x2 && nodeCheck.getNodeConf().getY() == y2)
				node2 = nodeCheck;
		}
		
		if(node1.getID() != -1 && node2.getID() != -1) {
			return findShortestPath(node1.getID(),node2.getID());
		}
		System.out.println("FindShortestPath innan den returnerar NULL");
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
	
	public void resetMap(){
		graph = new Graph();
	}
	

}
