package server;

import java.util.*;
import java.lang.*;


import brite.Graph.*;


public final class Dijkstra {
	private HashMap d;
	/** d[v] constains shortest distance from source to node v */
	private HashMap p;
	/**
	 * p[v] constains immediate predecessor of v in shortest parth from source.
	 */
	Node src;
	Graph g;
	Node[] nodes;
	ArrayList pQ;
	ArrayList S;

	public HashMap runDijkstra(Graph g, Node src) {
		this.g = g;
		this.src = src;
		this.nodes = g.getNodesArray();
		InitializeSingleSource(g, src);
		S = new ArrayList(nodes.length);
		while (!pQ.isEmpty()) {
			Node u = (Node) pQ.remove(0);
			S.add(u);
			Node[] neighbors = g.getNeighborsOf(u);
			for (int j = 0; j < neighbors.length; ++j) {
				Edge e = g.getEdge(u, neighbors[j]);
				double w = 1.0;
				try {
					w = (double) e.getEuclideanDist();
				} catch (Exception ex) {
					System.out.println("****  in Dijkstra, edge " + e + " does not have euclidean dist");
					System.out.println(ex);
				}
				Relax(u, neighbors[j], w);
			}
		}
		return p;
	}

	private void InitializeSingleSource(Graph g, Node s) {
		this.src = src;
		this.g = g;
		d = new HashMap(nodes.length);
		p = new HashMap(nodes.length);
		pQ = new ArrayList(nodes.length);
		Double max = new Double(Double.MAX_VALUE);
		for (int i = 0; i < nodes.length; ++i) {
			d.put(nodes[i], max);
			p.put(nodes[i], null);
			if (nodes[i] != s)
				pQ.add(nodes[i]);
		}
		d.put(s, new Double(0.0));
		pQ.add(0, s); // pQ is sorted by distance
	}

	private void Relax(Node u, Node v, double w) {
		double dv = ((Double) d.get(v)).doubleValue();
		double du = ((Double) d.get(u)).doubleValue();
		if (du != Double.MAX_VALUE && dv > du + w) {
			d.put(v, new Double(du + w));
			p.put(v, u);
			UpdatePQ(v, du + w); // ensure that PQ remains sorted
		}
	}

	

	// --------------- Priority Queue Helper Functons -----------------------

	private void UpdatePQ(Node v, double newWeight) {
		// first check if v is in PQ, if not don't worry about it.
		// ** Note that the if v is in S then v is not in PQ.
		int vIndex;
		// its faster to check the smaller arraylist
		if (pQ.size() > S.size()) { // Check S first since its smaller
			vIndex = S.indexOf(v);
			if (vIndex != -1) // S contains it, so pQ doesn't. No update reqd.
				return;
		}
		vIndex = pQ.indexOf(v);
		if (vIndex == -1)
			return;
		pQ.remove(vIndex);
		int newIndex = binarySearch(newWeight, 0, pQ.size());
		pQ.add(newIndex, v);
	}

	private int binarySearch(double newWeight, int beginIndex, int endIndex) {
		while (beginIndex != endIndex && endIndex >= beginIndex) {
			int mid = (int) (beginIndex + endIndex) / 2;
			double midWeight = ((Double) d.get((Node) pQ.get(mid))).doubleValue();
			if (midWeight < newWeight)
				beginIndex = mid + 1;
			else if (midWeight > newWeight)
				endIndex = mid - 1;
			else
				return mid; // found!
		}
		// boundary conditions :
		if (endIndex < 0)
			return 0;
		if (endIndex > pQ.size())
			return pQ.size();
		if (endIndex != pQ.size()) {
			double endWeight = ((Double) d.get((Node) pQ.get(endIndex))).doubleValue();
			if (newWeight > endWeight)
				return endIndex + 1;
		}
		return endIndex;

	}

}