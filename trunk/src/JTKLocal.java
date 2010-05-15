
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

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
import java.util.concurrent.*;

public class JTKLocal implements Runnable{

	JTKMap map;
	SonarModel sonar;
	private int N;

	Random rand;
	private long lasttime;
	private double time;

	Sample[] S;

	private double speed;
	private double turnrate;
	private double[] sp;

	private Lock lock;
	private boolean isready = false;
	private boolean firsttime = true;

	private double lastx;
	private double lasty;
	private double lasth;
	private double curx;
	private double cury;
	private double curh;

	public double average_x;
	public double average_y;
	public double average_h;

	private double threshold = .5;

	public boolean localized = false;

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
		
		lastx = 0;
		lasty = 0;
		lasth = 0;

		//for(int i=0;i<N;i++)
		//	S[i] = new Sample(); //random
		for(int i=0;i<N;i++) {
			switch(i%8) {
			case 0:
				S[i] = new Sample(-15.5,12.,0);
				break;
			case 1:
				S[i] = new Sample(-16.5,12.,180);
				break;
			case 2:
				S[i] = new Sample(-5.,-10.5,0);
				break;
			case 3:
				S[i] = new Sample(7.5,1.,90);
				break;
			case 4:
				S[i] = new Sample(-48.,12.,90);
				break;
			case 5:
				S[i] = new Sample(-48.,-10.5,270);
				break;
			case 6:
				S[i] = new Sample(7.5,-5.,90);
				break;
			case 7:
				S[i] = new Sample(0.,-7.,270);
				break;
			}
		}

		lasttime = System.currentTimeMillis();
		lock = new ReentrantLock();
		
	}

	public synchronized void update(double x, double y, double h, double sp[]) {
		if(!isready && lock.tryLock()) {
			//this.speed = speed;
			//this.turnrate = turnrate;
			
			this.curx = x;
			this.cury = y;
			this.curh = h;
			if(firsttime) {
				this.lastx = x;
				this.lasty = y;
				this.lasth = h;
				firsttime = false;
			}

			this.sp = sp;
			isready = true;
			lock.unlock();
		}
	}

	// function implemented out of:
	// Artificial Intelligence, 3rd Ed, 
	// by Russel & Norvig, page 982.
	public synchronized void _update() {

		Sample T[] = new Sample[N];
		double W[] = new double[N];

		long now = System.currentTimeMillis();
		time = (double)(now - lasttime) / 1000.; 
		lasttime = now;

		double totalW = 0.;
		double conf = 0.;
		for(int i=0;i<N;i++) {
			T[i] = S[i].motion(curx-lastx,cury-lasty,curh-lasth);
			W[i] = T[i].probability(sp);
			//if(T[i].obstacle()) {
			////if(W[i] < .01) {
			//	T[i] = new Sample(); 
			//	W[i] = T[i].probability(sp);
			////}
			//}
			totalW += W[i];
			if(W[i] > conf) conf = W[i];
		}

		double CDF[] = new double[N];
		for(int i=0;i<N;i++) {
			double pastCDF = (i==0)?0.:CDF[i-1];
			CDF[i] = W[i] / totalW + pastCDF;
		}

		// sampling
		for(int i=0;i<N;i++) {
			//if(i < 100 && totalW < 20.) {
			//	S[i] = new Sample();
			//	continue;
			//}
			// random U(0,1)
			double p = rand.nextDouble();

			// linear
			int index=0;
			while(index < N && p > CDF[index])
				index++;
			if(index>=N) index=N-1;

			// pick number at location <-- that's the sampled value
			S[i] = T[index];
		}

		lastx = curx;
		lasty = cury;
		lasth = curh;

		average_x = 0.;
		average_y = 0.;
		average_h = 0.;

		for(int i=0;i<N;i++) {
			average_x += S[i].X;
			average_y += S[i].Y;
			average_h += S[i].H;
		}

		average_x /= (double)N;
		average_y /= (double)N;
		average_h /= (double)N;

		int outliers = 0;
		for(int i=0;i<N;i++) {
			if((average_x - S[i].X)+(average_y - S[i].Y) > threshold) 
				outliers++;
		}

		if(outliers < N/20)
			localized = true;

		//if(localized) {
		//	if(map.workspace(average_x,average_y)) {
		//		for(int i=0;i<N;i++)
		//			S[i] = new Sample(); //random
		//		localized = false;
		//	}
		//}

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
			this.X = X + rand.nextGaussian() * .05;
			this.Y = Y + rand.nextGaussian() * .05;
			this.H = H + rand.nextGaussian() * .01;
		}
		
		// H is in DEGREES if passed as INTEGER.
		public Sample(double X,double Y,int H) {
			this(X,Y,(double)H * Math.PI / 180.);
		}

		public Sample motion(double dx,double dy,double dh) {
			//double dist = speed * time;
			//double theta = turnrate * time;
			double dist = Math.sqrt(dx*dx + dy*dy);
			return new Sample(X + dist * Math.cos(H + dh/2.), 
				Y + dist * Math.sin(H + dh/2.), H+dh);
		}

		public boolean obstacle() {
			return map.workspace(X,Y);
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
			if(map.cspace(X,Y))
				prob = .1;

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

	@Override
	public void run() {
		while(true) {
			lock.lock();
			if(isready) {
				_update();
				isready = false;
			}
			lock.unlock();
		}
	}

}
