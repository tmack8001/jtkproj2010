
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
import java.util.Collection;
import java.util.LinkedList;

import java.util.Random;

public class JTKMapImage extends JPanel implements ImageProducer {

	private JTKMap map;
	private Image img = null;
	private List<ImageConsumer> iclist;
	private List<Point2D> points = null;
	private JTKLocal.Sample[] particles = null;
	private ColorModel cm;

	public static void main(String args[]) throws Exception {
		JFrame f = new JFrame("map");
		JTKMapImage jtk = new JTKMapImage(new JTKMap());

		f.add(jtk);

		f.pack();
		f.setVisible(true);

		List<Point2D> p1 = new LinkedList<Point2D>();
		p1.add(new Point2D.Double(-30.0,-9.0));
		p1.add(new Point2D.Double(-30.0,12.7));
		jtk.setPoints(p1);

		//   size of the world: [131.2 41]
		//List<Point2D> p2 = new LinkedList<Point2D>();
		//Random r = new Random();
		//for(int i=0;i<500;i++)
		//	p2.add(new Point2D.Double(
		//		(r.nextDouble()-.5)*(131.2),
		//		(r.nextDouble()-.5)*(41.)));

		SonarModel sonar = new SonarModel(10);

		JTKLocal local = new JTKLocal(sonar,5000,jtk.map);

		jtk.setParticles(local.S);

		for(int i=0;i<360;i+=10) {
			double theta = (double)i * Math.PI/180.;
			double dist = jtk.map.raycast(-30.,-9.,theta);
			System.out.println(i + "\t" + dist);
		}
	}

	public void setParticles(JTKLocal.Sample[] particles) {
		this.particles = particles;
		repaint();
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
		
		r[0]=g[0]=b[0]=(byte)255;
		r[1]=g[1]=b[1]=0;
		
		r[2]=(byte)255;
		g[2]=b[2]=0;

		
		cm = new IndexColorModel(8,256,r,g,b);

	}

	public Dimension getPreferredSize() {
		return new Dimension(800,250);
	}

	public void paintComponent(Graphics g) {
		if(img==null) img = createImage(this);
		Graphics2D g2d = (Graphics2D)g;
		double scaleW = (double)getWidth() / (double)img.getWidth(this);
		double scaleH = (double)getHeight() / (double)img.getHeight(this);

		g2d.scale(scaleW,scaleH);
		g.drawImage(img,0,0,null);

		g2d.scale(1./scaleW,1./scaleH);

		g.setColor(Color.GREEN);
		if(points != null) for(Point2D p : points) {
		   g.fillRect(
		      (int)(scaleW*JTKMap.point2pixels(p).getX())-2,
		      (int)(scaleH*JTKMap.point2pixels(p).getY())-2,
		      5,5);
		}

		g.setColor(Color.RED);
		if(particles != null) for(JTKLocal.Sample s : particles) {
			Point2D p = JTKMap.point2pixels(new Point2D.Double(s.X,s.Y));
			g.fillRect((int)(scaleW*p.getX()),
			           (int)(scaleH*p.getY()),
				   1,1);
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

		ic.setDimensions(1600,500);
		ic.setPixels(0,0,1600,500,cm,map.workspace,0,1600);

		// c-space display:
		//ic.setPixels(0,500,1600,500,cm,map.cspacebytes,0,1600);

		ic.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
	}

}
