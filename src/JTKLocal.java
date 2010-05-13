
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

// --- added for testing ---
import javaclient2.PlayerClient;
import javaclient2.Position2DInterface;
import javaclient2.SonarInterface;
import javaclient2.structures.PlayerConstants;
import javaclient2.structures.sonar.PlayerSonarData;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.List;
import java.util.Collection;
import java.util.LinkedList;

public class JTKLocal {

	JTKMap map;
	SonarModel sonar;
	private int N;

	Random rand;
	private long lasttime;
	private double time;

	Sample[] S;

	// sonar info yanked from pioneer.inc
	private double sonar_x[] =
		{0.075,0.115,0.150,0.170,0.170,0.150,0.115,0.075};
	private double sonar_y[] =
		{0.130,0.115,0.080,0.025,-0.025,-0.080,-0.115,-0.130};
	private double sonar_h[] = 
		{1.571,0.873,0.523,0.175,-.175,-.523,-.873,-1.571};
	

	public JTKLocal(SonarModel sonar,int N,JTKMap map) {
		this.map = map;
		this.sonar = sonar;
		this.N = N;
		rand = new Random();
		S = new Sample[N];

		for(int i=0;i<N;i++)
			S[i] = new Sample(); //random

		lasttime = System.currentTimeMillis();
		
	}

	// function implemented out of:
	// Artificial Intelligence, 3rd Ed, 
	// by Russel & Norvig, page 982.
	public Sample[] update(
			double speed,
			double turnrate,
			double[] sp) {

		Sample T[] = new Sample[N];
		double W[] = new double[N];

		long now = System.currentTimeMillis();
		time = (double)(now - lasttime) / 1000.; 
		lasttime = now;

		double totalW = 0.;
		for(int i=0;i<N;i++) {
			T[i] = S[i].motion(speed,turnrate);
			W[i] = T[i].probability(sp);
			totalW += W[i];
		}

		double CDF[] = new double[N];
		for(int i=0;i<N;i++) {
			double pastCDF = (i==0)?0.:CDF[i-1];
			CDF[i] = W[i] / totalW + pastCDF;
		}

		System.out.println("time: " + time + " CDF[N-1]: " + CDF[N-1]
			+ " totalW = " + totalW);
		
		// sampling
		for(int i=0;i<N;i++) {
			// random U(0,1)
			double p = rand.nextDouble();

			// binary search through CDF
			//int index = binarysearch(0,N,CDF,p);
			// linear
			int index=0;
			while(index < N && p > CDF[index])
				index++;
			if(index>=N) index=N-1;

			// pick number at location <-- that's the sampled value
			S[i] = T[index];
		}

		return S;
	}

	public int binarysearch(int start,int end,double array[],double target) {
		int index = start + (end-start)/2; 
		if(start >= N || end >= N) 
			return N-1;
		else if(end <= start)
			return start;
		else if(index < 0) 
			return 0;
		else if(array[index] > target)
			return binarysearch(start, index-1, array, target);
		else if(array[index] < target)
			return binarysearch(index+1, end, array, target);
		else
			return index; 
	}
			
	public class Sample {

		double X;
		double Y;
		double H;

		public Sample() {
			boolean obstacle = true;
			while(obstacle) {
				X = (rand.nextDouble() - .5) * 131.2;
				Y = (rand.nextDouble() - .5) * 41;
				obstacle = map.cspace(X,Y);
			}
			H = (rand.nextDouble() - .5) * 2. * Math.PI;
		}

		public Sample(double X,double Y,double H) {
			this.X = X + rand.nextGaussian() * .1;
			this.Y = Y + rand.nextGaussian() * .1;
			this.H = H + rand.nextGaussian() * .1;
		}

		public Sample motion(double speed,double turnrate) {
			double dist = speed * time;
			double theta = turnrate * time;
			// TODO: apply gaussian dist.
			// TODO: account for moving and turning together
			return new Sample(X + dist*Math.cos(H+theta), 
				Y + dist*Math.sin(H+theta), H+theta);
		}

		// sonar reading probability
		// (adapted from mapping code.)
		public double probability_shitty(double sp[]) {
			double prob = 1.;

			for(int i=0;i<8;i++) { // for each sonar
			  double x = X + sonar_x[i];
			  double y = Y + sonar_y[i];
			  double h = H + sonar_h[i];
			  for(double d=0;d<5.0;d+=0.2) {
			    for(double theta=-Math.PI/24.;
			               theta<Math.PI/24.;
			               theta+=Math.PI/(48.)) {
			      double obs_prob = sonar.PM_d_theta(sp[i],theta,d);
			      double x_map = Math.cos(h+theta)*d+X;
			      double y_map = Math.sin(h+theta)*d+Y;
			      if(map.workspace(x_map,y_map)) {
			        prob *= prob; //obstacle
			      } else {
			        prob *= (1-prob); //no obstacle
			      }
			    }
			  }
			}

			return prob;
		}

		public double probability(double sp[]) {
			double prob = 1.;

			for(int i=0;i<8;i++) { // for each sonar
			  double x = X + sonar_x[i];
			  double y = Y + sonar_y[i];
			  double h = H + sonar_h[i];

			  double d = map.raycast(x,y,h);

			  // should i swap the sp reading with the d value?!
			  double obs_prob = sonar.PM_d_theta(sp[i],0.,d);
			  prob *= obs_prob;
			  //System.err.println(" * " + (obs_prob));
			  //double x_map = Math.cos(h)*d+X;
			  //double y_map = Math.sin(h)*d+Y;

			  //if(map.workspace(x_map,y_map)) {
			  //  prob *= obs_prob; //obstacle
			  //  System.err.println(" * " + (obs_prob));
			  //} else {
			  //  prob *= (1-obs_prob); //no obstacle
			  //  System.err.println(" * " + (1-obs_prob));
			  //}
			} 
			//System.err.println(" = " + (prob));

			return prob;
		}

	}

	public static void main(String args[]) throws Exception {
		int port = 6665;
		String server = "192.168.1.107";
		
		JFrame f = new JFrame("JTKLocal - localization");
		JTKMap map = new JTKMap();
		JTKMapImage jtk = new JTKMapImage(map);
		f.add(jtk);
		f.pack();
		f.setVisible(true);

		List<Point2D> p1 = new LinkedList<Point2D>();
		p1.add(new Point2D.Double(-30.0,-9.0));
		p1.add(new Point2D.Double(-30.0,12.7));
		jtk.setPoints(p1);

		SonarModel sonarmodel = new SonarModel(10);
		JTKLocal local = new JTKLocal(sonarmodel,5000,map);
		jtk.setParticles(local.S);

		PlayerClient robot = new PlayerClient(server, port);
		SonarInterface sonar = robot.requestInterfaceSonar(0, 
				PlayerConstants.PLAYER_OPEN_MODE);
		Position2DInterface motor = robot.requestInterfacePosition2D(0, 
				PlayerConstants.PLAYER_OPEN_MODE);
		
		// turn stuff on.  this might not be necessary
		sonar.setSonarPower(1);
		motor.setMotorPower(1);
		
		double sp[] = new double[8];
		while (true) {
			float turnRate, speed;
			
			// read all the data
			robot.readAll();

			// don't do anything unless there's data
			
			if (sonar.isDataReady()) {
				PlayerSonarData sonarData = sonar.getData();
				float[] ranges = sonarData.getRanges();
				jtk.repaint();
				
				if (ranges.length == 0)
					continue;

				for(int i=0;i<8;i++) 
					sp[i] = ranges[i];
                
				if (ranges[0] + ranges[1] < ranges[6] + ranges[7])
					turnRate = -20.0f * (float)Math.PI / 180.0f;
				else
					turnRate = 20.0f * (float)Math.PI / 180.0f;
				
				if (ranges[3] < 0.5f)
					speed = 0.0f;
				else
					speed = 0.1f;

				//speed = 0f;
				//turnRate = 0f;
				
				// send the command
				motor.setSpeed(speed, turnRate);
				local.update((double)speed,(double)turnRate,sp);
			}
		}
	}

}
