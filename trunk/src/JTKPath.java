/**
 * filename: JTKPath.java
 */

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;

/**
 *  Class JTKPath: Path Planning for MRP project
 *  
 * @author Trevor Mack
 *
 */

public class JTKPath extends Thread {

	private Graph prob_roadmap;
	private JTKMap map;
	
	
	/**
	 * Default constructor. Creates a Local Planner and initializes the probabilistic roadmap.
	 * @param map - a given map (contains cspace and workspace interpretations)
	 */
	public JTKPath(JTKMap map) {
		this.map = map;
		initializeRoadMap();
	}
	
	/**
	 * Initializes the probabilistic roadmap as per algorithm #6 outlined
	 * in "Principles of Robot Motion" by H. Choset et. al. MIT Press
	 * 1. Initially empty Graph G
	 * 2. A configuration q is randomly chosen
	 * 3. If q->Q_free then added to G (collision detection needed here)
	 * 4. Repeat until N vertices chosen
	 * 5. For each q, select k closest neighbors 
	 * 6. Local planner delta connects q to neighbor q'
	 * 7. If connect successful (i.e. collision free local path), add edge (q, q')
	 */
	public void initializeRoadMap() {
		Random rand = new Random();
		//map is of 1600*500 size
		for(int i=0; i<20; i++) {
			int x = rand.nextInt(1600);
			int y = rand.nextInt(500);
			if(map.cspace(x, y)) {
				Point2D point = new Point2D.Double(x, y);
				prob_roadmap.addVertex(point);
				
				List<Point2D> closest = prob_roadmap.closestVertex(point, 2);
				//for each point in closest
				for( int j=0; j<closest.size(); j++) {
					if( planPath(point, closest.get(j)) ) {
						prob_roadmap.addEdge(point, closest.get(j));
					}
				}
				
			}
		}
	}
	
	/**
	 * 
	 * 
	 * @param p1	the origin point
	 * @param p2	the destination point
	 * @return		true, there is a "simple" path (will generate one if not one)
	 * 				false, there is no path from p1 -> p2 
	 */
	public boolean planPath(Point2D p1, Point2D p2) {
		return true;
	}

}