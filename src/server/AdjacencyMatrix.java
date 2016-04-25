package server;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyMatrix {
	
	private int nbrOfNodes;
	private boolean[][] edges;
	private String[] nodes;
	
	public AdjacencyMatrix (String[] nodes) {
		this.nbrOfNodes=nodes.length;
		edges=new boolean[nbrOfNodes][nbrOfNodes];
	}
	
	public void addEdge (int node1, int node2) {
		edges[node1][node2]=true;
		edges[node2][node1]=true;
	}
	
	public void removeEdge (int node1, int node2){
		edges[node1][node2]=false;
		edges[node2][node1]=false;	
	}
	
	public boolean hasEdge (int node1, int node2){
		return edges[node1][node2];
	}
	
	public String getValue (int node) {
		return nodes[node];
	}
	
	public List<String> getShortestRoute (int start, int end){
		List<String> shortestRoute = new ArrayList<String>();
		
		
		return shortestRoute;	
	}
	
	public List<Integer> findChildren (int currentNode, int endNode, List<Integer> visitedNodes){
		visitedNodes.add(currentNode);
		
		List<Integer> children = new ArrayList<Integer>();
		
		for(int i = 0; i < nbrOfNodes; i++)
			if(edges[currentNode][i])
				children.add(i);
		
		for (Integer I : children){
			if (I==endNode){
				return visitedNodes;
			}
			else  
				return findChildren(I, endNode, visitedNodes);
		}
		
		return null;
	}
}
