
import java.io.File;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.*;

import java.awt.geom.Point2D;

// from project.world: the size of a pixel in meters 0.02
//                     size of the world: [131.2 41]
// from pioneer.inc: actual size [0.44 0.33] (assuming in meters)

public class JTKMap {

	/** workspace in bytes (for drawing) */
	public byte[] workspace;
	/** cspace in bytes (for drawing) ... might delete this. */
	public byte[] cspacebytes;
	private boolean[] cspace;
	private double radius = 5.0; // in pixels

	public JTKMap() {
		File f = new File("3large.raw");
		workspace = new byte[1600*500];
		cspacebytes = new byte[1600*500];
		cspace = new boolean[1600*500];
		try { 
			DataInputStream in = new DataInputStream(
				new FileInputStream(f));
			for(int i=0;i<1600*500;i++) {
				workspace[i] = in.readByte();
				cspace[i] = false;
			}
		} catch(IOException e) {
			System.out.println("Error reading map: " + e);
		}
		System.out.println("Robot: ");
	        for(int m=-5;m<6;m++) {
			for(int n=-5;n<6;n++) {
				System.out.print(
					Math.sqrt(m*m + n*n)<radius?"#":" ");
			}
			System.out.println();
		}
		makecspace();
		for(int i=0;i<1600*500;i++) {
			cspacebytes[i]=(byte)(cspace[i]?1:0);
		}
	}

	/** get coordinate (x,y) in workspace map [meters]
	 *  @return byte representation of workspace coordinate */
	public boolean workspace(double x, double y) {
		return workspace((int)Math.floor(1600.*x / 131.2 + (1600./2.)),
		                 500-(int)Math.floor(500.*y / 41. + (500./2.)));
	}

	/** get coordinate (x,y) in workspace map 
	 *  @return byte representation of workspace coordinate */
	public boolean workspace(int x, int y) {
		if(y >= 500) y = 499;
		if(x >= 1600) x = 1599;
		if(y < 0) y = 0;
		if(x < 0) x = 0;
		return workspace[1600*y+x] < 0;
	}

	/** get coordinate (x,y) in cspace map [meters]
	 *  @return obstacle status */
	public boolean cspace(double x, double y) {
		return cspace((int)Math.floor(1600.*x / 131.2 + (1600./2.)),
		              500-(int)Math.floor(500.*y / 41. + (500./2.)));
	}

	/** get coordinate (x,y) in cspace map 
	 *  @return obstacle status */
	public boolean cspace(int x, int y) {
		if(y >= 500) y = 499;
		if(x >= 1600) x = 1599;
		if(y < 0) y = 0;
		if(x < 0) x = 0;
		return cspace[1600*y+x];
	}

	/** change point to pixel representation
	 *  @return obstacle status */
	public static Point2D point2pixels(Point2D p) {
		Point2D q = new Point2D.Double(
			 Math.floor(1600.*p.getX() / 131.2 + (1600./2.)),
		         500 - Math.floor(500.*p.getY()/ 41. + (500./2.)));
		return q;
	}

	private void makecspace() {
	  for(int i=0; i<1600; i++) {
	    for(int j=0; j<500; j++) {
	      if(workspace(i,j)) {
	        for(int m=-5;m<6;m++) for(int n=-5;n<6;n++) {
	          if(i+m >= 0 && j+n >= 0 && i+m < 1600 && j+n < 500)
	             if(Math.sqrt(m*m + n*n) < radius)
	               cspace[1600*(j+n)+(i+m)] = true;
	        }
	      }
	    }
	  }
	}

	// integer absolute value
	private int abs(int x) {
		return x<0? x*-1 : x;
	}

	// Bresenham line drawing algorithm, Wikipedia:
	// http://en.wikipedia.org/wiki/Bresenham's_line_algorithm
	private Pixel collision(int x0, int x1, int y0, int y1) {
		boolean steep = abs(y1 - y0) > abs(x1 - x0);
		boolean backwards = false;
		int tmp;
		if(steep) {
			//swap(x0, y0);
			tmp = x0;
			x0 = y0;
			y0 = tmp;

			//swap(x1, y1);
			tmp = x1;
			x1 = y1;
			y1 = tmp;
		}
		if(x0 > x1) {
			backwards = true;

			//swap(x0, x1);
			tmp = x0;
			x0 = x1;
			x1 = tmp;

			//swap(y0, y1);
			tmp = y0;
			y0 = y1;
			y1 = tmp;
		}
		int deltax = x1 - x0;
		int deltay = abs(y1 - y0);
		int error = deltax / 2;
		int ystep;
		int y = y0;
		if(y0 < y1) ystep = 1; else ystep = -1;
		Pixel hit = null;
		for(int x=x0;x<x1;x++) {
			if(x < 0 || x >= 1600 || y < 0 || y >= 500) break;
			if(steep) {
				if(workspace(y,x)) {
					hit = new Pixel(y,x);
					if(!backwards) 
						break;
				}
			} else {
				if(workspace(x,y)) {
					hit = new Pixel(x,y);
					if(!backwards) 
						break;
				}
				error = error - deltay;
			}
			if(error < 0) {
				y = y + ystep;
				error = error + deltax;
			}
		}
		return hit;
	}

	public class Pixel {
		int x;
		int y;
		public Pixel(int _x,int _y) { x=_x; y=_y; }
		public Pixel(Point2D _p) {
			Point2D p = point2pixels(_p);
			x = (int)p.getX();
			y = (int)p.getY();
		}
		public String toString() {
			return "(" + x + "," + y + ")";
		}
	}

	public double raycast(double x,double y,double theta) {
		Pixel p1 = new Pixel(new Point2D.Double(x,y));
		//Pixel p2 = m.new Pixel(new Point2D.Double(-45.,10.));
		Pixel p2 = new Pixel(new Point2D.Double(
			x+Math.cos(theta)*5.,
			y+Math.sin(theta)*5.));
		
		Pixel p3 = collision(p1.x,p2.x,p1.y,p2.y);

		//System.out.print(p1 + " -> " + p2);
		//if(p3 == null)
		//	System.out.print(" NO HIT!");
		//else
		//	System.out.print(" HIT: "+p3);

		double dist = 5.;
		if(p3 != null) {
			double dx = (double)(p3.x - p1.x) * 131.2/1600.;
			double dy = (double)(p3.y - p1.y) * 41.0/500;
			dist = Math.sqrt( dx*dx + dy*dy );
		}
		if(dist > 5.) dist = 5.;
		return dist;
	}

	public static void main(String args[]) {
		JTKMap m = new JTKMap();
		
		for(int i=0;i<360;i+=10) {
			double theta = (double)i * Math.PI/180.;
			double dist = m.raycast(-48.,12.,theta);
			System.out.println(" DISTANCE: "+dist);
		}


	}


}
