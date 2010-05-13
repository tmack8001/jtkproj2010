package view;
import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Dimension;
import java.awt.RenderingHints;


public class SonarView extends JPanel {
	private float sp[] = { 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f };
	private int width = 400;
	int pioneer_x[] = { 23,15,-15,-23,-23,-15,15,23 };
	int pioneer_y[] = { 05,15,15,05,-05,-15,-15,-05 };
	// spose[0] [ 0.075 0.130 90 ]
	// spose[1] [ 0.115 0.115 50 ]
	// spose[2] [ 0.150 0.080 30 ]
	// spose[3] [ 0.170 0.025 10 ]
	// spose[4] [ 0.170 -0.025 -10 ]
	// spose[5] [ 0.150 -0.080 -30 ]
	// spose[6] [ 0.115 -0.115 -50 ]
	// spose[7] [ 0.075 -0.130 -90 ]
	int sonar_x[] = { 8, 12, 15, 17, 17, 15, 12,  8};	
	int sonar_y[] = {13, 11,  8,  2, -2, -8,-11,-13};
	int sonar_h[] = {90, 50, 30, 10, -10, -30, -50, -90};


	public SonarView() {
		super();
	}

	public void updateSonars(float[] sonars) {
		sp = sonars;
		repaint();
		this.requestFocus();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		//g2d.translate(000,400);
		//g2d.rotate(-Math.PI/2.0);
		int halfheight = getHeight()/2;

		Polygon pioneer = new Polygon(pioneer_x,pioneer_y,8);
		pioneer.translate(0,halfheight+2);
		
		g.setColor(Color.RED);
		g.drawPolygon(pioneer);

		g.setColor(Color.YELLOW);
		for(int i=0;i<8;i++) {
			g.drawOval(4+sonar_x[i],halfheight+sonar_y[i],4,4);
		}
		for(int i=0;i<8;i++) {
			double radius = 50.0*sp[i];
			int endpt_x1 = 
			(int)(radius*Math.cos((sonar_h[i]-7.5) * Math.PI/180.0))
				+4+sonar_x[i];
			int endpt_y1 = 
			(int)(radius*Math.sin((sonar_h[i]-7.5) * Math.PI/180.0))
				+halfheight+sonar_y[i];	
			int endpt_x2 = 
			(int)(radius*Math.cos((sonar_h[i]+7.5) * Math.PI/180.0))
				+4+sonar_x[i];
			int endpt_y2 = 
			(int)(radius*Math.sin((sonar_h[i]+7.5) * Math.PI/180.0))
				+halfheight+sonar_y[i];	
			
			
			g.setColor(Color.GRAY);
			g.drawLine(4+sonar_x[i],halfheight+sonar_y[i],
				   endpt_x1, endpt_y1);
			g.drawLine(4+sonar_x[i],halfheight+sonar_y[i],
				   endpt_x2, endpt_y2);
			g.drawLine(endpt_x1,endpt_y1,endpt_x2,endpt_y2);
			
			char[] reading = (new Double(sp[i]))
					.toString().toCharArray();
			endpt_x1 = (endpt_x1 + endpt_x2) / 2;
			endpt_y1 = (endpt_y1 + endpt_y2) / 2;
			if(endpt_x1 > getWidth()-40) endpt_x1 = getWidth()-40;
			if(endpt_y1 > getHeight()-10) endpt_y1 = getHeight()-10;
			if(endpt_y1 < 10)  endpt_y1 = 10;
			g.setColor(Color.BLACK);
			g.drawChars(reading,0,
				(reading.length > 5 ? 5 : reading.length),
				endpt_x1,endpt_y1);

			
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(200,400);
	}

}
