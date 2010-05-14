
import java.awt.geom.Point2D;

import java.util.List;

public class Goto {
	
	private int i = 0;
	private List<Point2D> points;
	private double dist = 9999;

	public double speed = 0;
	public double turnrate = 0;

	public Goto(List<Point2D> points) {
		this.points = points;
	}

	public boolean done() {
		return i == points.size();
	}

	public void update(double xpos, double ypos, double currentangle) {
		if(dist < .01) {
			i++;
		}
		if(i >= points.size()) {
			
		}

		double xdiff = xpos - points.get(i).getX();
		double ydiff = ypos - points.get(i).getY();

		double angle = Math.atan2(ydiff,xdiff);
		//double angle = atan2(points[i].y,points[i].x);

		dist = Math.sqrt( xdiff*xdiff + ydiff*ydiff );

		double adiff = angle - currentangle + 2*Math.PI;
		adiff = adiff - (2*Math.PI)*Math.floor(adiff/(2*Math.PI)) - Math.PI;
		if(adiff > Math.PI) {
			adiff += -2*Math.PI;
		} else if(adiff < -Math.PI) {
			adiff += 2*Math.PI;
		}

		if(Math.abs(adiff) > .15) {
			if(Math.abs(adiff) < 1.0) {
				turnrate = 4*Math.abs(adiff)+.1;
			} else {
				speed = 0.0;
				turnrate = 0.5;
			}
		} else {
			if(dist < .5)
				speed = .05;
			else if(dist > 1)
				speed = .5;
			else
				speed = (dist - .5)*.45 + .05;
		}

	}

}

