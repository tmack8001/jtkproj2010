/**
 * filename: Graph.java
 */

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Class Graph
 * 
 * @author Trevor Mack
 * @author David Sweeney
 *
 */
public class Graph {

	protected HashMap<Point2D, List<Point2D>> adjacencyMap;
	
	/**
	* Initialize this Graph Point2D to be empty.
	*/
	public Graph() {
		adjacencyMap = new HashMap<Point2D, List<Point2D>>();
	}
	
	/**
	 * Create a Graph from a file Point2D. File format are line delimited edges,
	 * where edges are space delimited vertices.
	 * 
	 * @param edges - a list of edges
	 */
	public Graph( List<Point2D[]> edges ) {
		this();
		//for each edge
		for( int i=0; i<edges.size(); i++) {
			Point2D[] edge = edges.get(i);
			if(edge.length != 2) {
				continue;
			}
			this.addEdge(edge[0], edge[1]);
		}
	}
	
	/**
	* Determines if this Graph contains no vertices.
	*
	* @return true - if this Graph contains no vertices, otherwise false
	*/
	public boolean isEmpty() {
		return adjacencyMap.isEmpty();
	}
	
	/**
	* Determines the number of vertices in this Graph.
	*
	* @return the number of vertices.
	*/
	public int size() {
		return adjacencyMap.size();
	}
	
	/**
	* Returns the number of edges in this Graph Point2D.
	*
	* @return the number of edges.
	*/
	public int getEdgeCount() {
		int count = 0;
		//iterate over the hashmap's keys counting edges
		Iterator<Point2D> it = adjacencyMap.keySet().iterator();
		while( it.hasNext() ) {
			List<Point2D> edges = (ArrayList<Point2D>)adjacencyMap.get(it.next());
			count += edges.size();
		}
		return count;
	}
	
	/**
	* Adds a specified Point2D as a vertex
	*
	* @param vertex - the specified Point2D
	* @return true  - if Point2D was added by this call, 
	* 		  false - if the Point2D already exists
	*/
	public boolean addVertex (Point2D vertex) {
		if (adjacencyMap.containsKey(vertex))
			return false;
		adjacencyMap.put (vertex, new ArrayList<Point2D>());
		return true;
	}
	
	/**
	* Adds an edge, and vertices if not already present
	*
	* @param v1 	- the beginning vertex Point2D of the edge
	* @param v2 	- the ending vertex Point2D of the edge
	* @return true 	- if the edge was added by this call
	*/
	public boolean addEdge (Point2D v1, Point2D v2) {
		addVertex (v1); addVertex (v2);
		adjacencyMap.get(v1).add(v2);
		adjacencyMap.get(v2).add(v1);
		return true;
	}
	
	/**
	 * Tests to see if 2 vertices are connected by exacted one edge.
	 * 
	 * @param v1	- the first vertex of the "edge"
	 * @param v2	- the second vertex of the "edge"
	 * @return true	- if v1 and v2 are connected by 1 edge
	 */
	public boolean isEdge (Point2D v1, Point2D v2) {
		return getNeighbors(v1).contains(v2);
	}
	
	/**
	 * Finds and returns all adjacent nodes to a vertex.
	 * 
	 * @param v		- the vertex to find neighbors of
	 * @return list	- the list of neighbors
	 */
	public List<Point2D> getNeighbors (Point2D v) {
		return adjacencyMap.get(v);
	}
	
	/**
	 * This is a stupid distance search for the closest unconnected vertex.
	 * 
	 * @param v		the vertex to look for the closest vertex of
	 * @param k		the number of closest vertices to find
	 * @return 		the closest unconnected vertex to obj
	 */
	public List<Point2D> closestVertex (Point2D v, int k) {
		List<Point2D> closest = new LinkedList<Point2D>();
		
		//initialize min/max distances
		double minDist = Double.MAX_VALUE;
		double maxDist = Double.MAX_VALUE;
		
		//iterate through list of points
		Iterator<Point2D> iter = adjacencyMap.keySet().iterator();
		while(iter.hasNext()) {
			Point2D temp = iter.next();
			if( !isEdge(v, temp) && v.distance(temp) < minDist ) {
				minDist = v.distance(temp);
				closest.add(0, temp);
			}else if(v.distance(temp) > minDist && v.distance(temp) < maxDist) {
			}
		}
		return closest;
	}
	
}