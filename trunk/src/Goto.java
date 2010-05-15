
import java.awt.geom.Point2D;

import java.util.List;

public class Goto {
	
	private int i = 0;
	private List<Point2D> points;
	private double dist = 9999;

	public double speed = 0;
	public double turnrate = 0;

	public final double THRESHOLD = .5;

	public Goto(List<Point2D> points) {
		this.points = points;
	}

	public boolean done() {
		return i == points.size();
	}

	public void update(double xpos, double ypos, double currentangle,double sp[]) {
		if(dist < .2) {
			System.out.println(">>>>>>>> REACHED POINT" + i + ": " + points.get(i));
			System.out.println("=================================================");
			i++;
		}

		if(i >= points.size()) {
			speed = 0.;
			turnrate = 0.;
			System.out.println("DONE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.exit(0);
			return;
		}

		double xdiff = xpos - points.get(i).getX();
		double ydiff = ypos - points.get(i).getY();

		dist = Math.sqrt(xdiff*xdiff + ydiff*ydiff);

		double angle = Math.atan2(dist * Math.sin(currentangle),dist * Math.cos(currentangle));

		double newturnrate = 0.;
		double newspeed = 0.;

		if(Math.abs(angle) > 10. * Math.PI/180.) {
			if(angle < 0)
				newturnrate = -20.0 * Math.PI / 180.0;
			else
				newturnrate = 20.0 * Math.PI / 180.0;
		}

		if(sp[3] < 0.5 || sp[4] < 0.5)
			newspeed = 0.0;
		else
			newspeed = 0.5;

		speed = newspeed;
		turnrate = newturnrate;

	}

}

