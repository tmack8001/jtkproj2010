/**
 * filename: JTKPath.java
 */

import java.awt.geom.Point2D;
import java.awt.image.SampleModel;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

/**
 *  Class JTKPath: Path Planning for MRP project
 *  
 * @author Trevor Mack
 *
 */

public class JTKPath extends Thread {

	private final String MAP_FILENAME = "initial_map";
	private Graph prob_roadmap;
	private JTKMap map;
	private JTKMapImage mapImage;
	
	
	/**
	 * Default constructor. Creates a Local Planner and initializes the probabilistic roadmap.
	 * @param map - a given map (contains cspace and workspace interpretations)
	 */
	public JTKPath(JTKMap map) {
		if(map == null) {
			this.map = new JTKMap();
		}else {
			this.map = map;
		}

		System.out.println("map created");
		mapImage = new JTKMapImage(this.map);
		
		prob_roadmap = new Graph();
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
		//add initial map points
		try {
			BufferedReader reader = 
				new BufferedReader(new FileReader(MAP_FILENAME));
	        String line = null;
	        while ( (line=reader.readLine()) != null) {
	        	String[] temp = line.split(" ");
	        	if( temp.length == 2 ) {
	        		Point2D curPoint = new Point2D.Double();
	        		curPoint.setLocation(Double.parseDouble(temp[0]), 
	            		Double.parseDouble(temp[1]));
	        		//add point from file to roadmap
	        		if(!map.cspace(curPoint.getX(), curPoint.getY())) {
		        		prob_roadmap.addVertex(curPoint);
		        		System.out.println("adding: " + curPoint.toString());
	        		}
	        	}
	        }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("FileNotFound thrown");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IOException thrown");
		}
		
		mapImage.setPoints(prob_roadmap.getVertices());
		
		//attempt to connect "close" points
		List<Point2D> points = prob_roadmap.getVertices();
		for( int i=0; i<points.size(); i++ ) {
			List<Point2D> closest = prob_roadmap.closestVertices(points.get(i), 10);
			for( int j=0; j<closest.size(); j++ ) {
				//try to connect points[i] to closest[j]
				if( planPath(points.get(i), closest.get(j)) ) {
					System.out.println("connected " + points.get(i) + " to " + closest.get(j));
					prob_roadmap.addEdge(points.get(i), closest.get(j));
					mapImage.setPRM(prob_roadmap);
				}else {
					System.out.println("could not plan a path between " + points.get(i) + " and " + closest.get(j));
				}
			}
		}
		
		Random rand = new Random();
		double X = 0, Y = 0;
		boolean obstacle = true;
		for(int i=0; i<1000; i++) {
			while(obstacle) {
				X = (rand.nextDouble() - .5) * 131.2;
				Y = (rand.nextDouble() - .5) * 41;
				obstacle = map.cspace(X,Y);
			}
			obstacle = true;
			Point2D curPoint = new Point2D.Double();
    		curPoint.setLocation(X,Y);
			this.addPoint(curPoint);
		}
	}
	
	/**
	 * Add a point to the graph and connect it to some "close" vertices.
	 * @param curPoint
	 */
	public void addPoint(Point2D curPoint) {
		if(!map.cspace(curPoint.getX(), curPoint.getY())) {
    		prob_roadmap.addVertex(curPoint);
    		System.out.println("adding: " + curPoint.toString());
		}
		
		List<Point2D> closest = prob_roadmap.closestVertices(curPoint, 5);
		for( int j=0; j<closest.size(); j++ ) {
			//try to connect points[i] to closest[j]
			if( planPath(curPoint, closest.get(j)) ) {
				System.out.println("connected " + curPoint + " to " + closest.get(j));
				prob_roadmap.addEdge(curPoint, closest.get(j));
				mapImage.setPRM(prob_roadmap);
			}else {
				System.out.println("could not plan a path between " + curPoint + " and " + closest.get(j));
			}
		}
	}
	
	/**
	 * This method will try and connect p1 and p2 together in the cspace using 
	 * a "subdivision" local planner algorithm using a binary search decomposition. 
	 * The algorithm tests the midpoints for an obstacle, if so then returns failure.
	 * 
	 * @param p1	the origin point
	 * @param p2	the destination point
	 * @return		true, there is a "simple" path (will generate one if not one)
	 * 				false, there is no path from p1 -> p2 
	 */
	public boolean planPath(Point2D p1, Point2D p2) {
		Point2D midpoint = JTKPath.midPoint(p1, p2);
		if(midpoint.distance(p1) < 0.01) {
			return true;
		}else if( map.cspace(midpoint.getX(), midpoint.getY())) {
			return false;
		}
		return planPath(p1, midpoint) && planPath(p2, midpoint);
	}
	
	public static Point2D midPoint(Point2D a, Point2D b) {
        return new Point2D.Double((a.getX() + b.getX()) / 2.0, (a.getY() + b
                .getY()) / 2.0);
    }
	
	public static void main(String[] args) {
		JFrame f = new JFrame("JTKProj2010 - MRP Path Planning");

		JTKPath path = new JTKPath(new JTKMap());		
		f.add(path.mapImage);
		
		f.pack();
		f.setVisible(true);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	}
}