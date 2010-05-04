
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.ImageIO;

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
	private ColorModel cm;

	public static void main(String args[]) throws Exception {
		JFrame f = new JFrame("map");
		JTKMapImage jtk = new JTKMapImage(new JTKMap());

		f.add(jtk);

		f.pack();
		f.setVisible(true);
	}

	public JTKMapImage(JTKMap map) {
		iclist = new LinkedList<ImageConsumer>();
		this.map = map;

		byte[] b = new byte[256];
		for(int i=0;i<256;i++) {
			b[255-i]=(i>127) ? (byte)(i - 256): (byte)i;
		}
		cm = new IndexColorModel(8,256,b,b,b);

	}

	public Dimension getPreferredSize() {
		return new Dimension(800,500);
	}

	public void paintComponent(Graphics g) {
		((Graphics2D)g).scale(
			(double)getWidth()/1600.,
			(double)getHeight()/1000.);
		g.drawImage(createImage(this),0,0,null);
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
		ic.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
	}

}
