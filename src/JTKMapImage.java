
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;

import java.io.File;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.*;

import java.util.List;
import java.util.LinkedList;

public class JTKMapImage extends JPanel implements ImageProducer {

	private JTKMap map;
	private List<ImageConsumer> iclist;
	private List<Point2D> points = null;
	private ColorModel cm;

	public static void main(String args[]) throws Exception {
		JFrame f = new JFrame("map");
		JTKMapImage jtk = new JTKMapImage(new JTKMap());

		f.add(jtk);

		f.pack();
		f.setVisible(true);

		List<Point2D> p = new LinkedList<Point2D>();
		p.add(new Point2D.Double(-30.0,-9.0));
		p.add(new Point2D.Double(-30.0,12.7));
		jtk.setPoints(p);
	}

	public void setPoints(List<Point2D> points) {
		this.points = points;
		repaint();
	}

	public JTKMapImage(JTKMap map) {
		iclist = new LinkedList<ImageConsumer>();
		this.map = map;

		byte[] r = new byte[256];
		byte[] g = new byte[256];
		byte[] b = new byte[256];
		
		r[0]=g[0]=b[0];
		r[1]=g[1]=b[1]=(byte)255;
		
		r[2]=(byte)255;
		g[2]=b[2]=0;

		
		cm = new IndexColorModel(8,256,r,g,b);

	}

	public Dimension getPreferredSize() {
		return new Dimension(800,500);
	}

	public void paintComponent(Graphics g) {
		((Graphics2D)g).scale(
			(double)getWidth()/1600.,
			(double)getHeight()/1000.);
		g.drawImage(createImage(this),0,0,null);
		g.setColor(Color.GREEN);
		if(points != null) for(Point2D p : points) {
			g.drawRect((int)JTKMap.point2pixels(p).getX(),
			           (int)JTKMap.point2pixels(p).getY(),
				   5,5);
		}
			
	}

	public void addConsumer(ImageConsumer ic) {
		iclist.add(ic);
		ic.setColorModel(cm);
	}

	public boolean isConsumer(ImageConsumer ic) {
		return iclist.contains(ic);
	}

	public void removeConsumer(ImageConsumer ic) {
		iclist.remove(ic);
	}

	public void requestTopDownLeftRightResend(ImageConsumer ic) {
	}

	public void startProduction(ImageConsumer ic) {
		addConsumer(ic);
		ic.setDimensions(1600,1000);
		ic.setPixels(0,0,1600,500,cm,map.workspace,0,1600);
		ic.setPixels(0,500,1600,500,cm,map.cspacebytes,0,1600);
		//byte[] b = new byte[100];

		//for(int i=0;i<100;i++)
		//	b[i] = (byte)2;

		//ic.setPixels(10,10,10,10,cm,b,0,10);

		ic.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
	}

}
