
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

	public static void main(String args[]) {
		new JTKMap();
	}

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
			workspace[i]=(byte)(workspace[i]<0?1:0);
		}
	}

	/** get coordinate (x,y) in workspace map [meters]
	 *  @return byte representation of workspace coordinate */
	public boolean workspace(double x, double y) {
		return workspace((int)Math.floor(1600.*x / 131.2 + (1600./2.)),
		                 (int)Math.floor(500.*y / 41. + (500./2.)));
	}

	/** get coordinate (x,y) in workspace map 
	 *  @return byte representation of workspace coordinate */
	public boolean workspace(int x, int y) {
		return workspace[1600*y+x] < 0;
	}

	/** get coordinate (x,y) in cspace map [meters]
	 *  @return obstacle status */
	public boolean cspace(double x, double y) {
		return cspace((int)Math.floor(1600.*x / 131.2 + (1600./2.)),
		              (int)Math.floor(500.*y / 41. + (500./2.)));
	}

	/** get coordinate (x,y) in cspace map 
	 *  @return obstacle status */
	public boolean cspace(int x, int y) {
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

}
