
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

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
		double pastCDF = 0.;
		for(int i=0;i<N;i++) {
			CDF[i] = W[i] / totalW + pastCDF;
			pastCDF += CDF[i];
		}

		System.out.println("integral of CDF: " + pastCDF);
		
		// sampling
		for(int i=0;i<N;i++) {
			// random U(0,1)
			double p = rand.nextDouble();

			// binary search through CDF
			int index = binarysearch(0,N,W,p);

			// pick number at location <-- that's the sampled value
			S[i] = T[index];
		}

		return S;
	}

	public int binarysearch(int start,int end,double array[],double target) {
		int index = start + (start-end)/2; 
		if(end <= start)
			return start;
		else if(array[index] > target)
			return binarysearch(start, index-1, array, target);
		else if(array[index] < target)
			return binarysearch(index+1, end, array, target);
		else
			return index; //unlikely!
	}
			
	public class Sample {

		double X;
		double Y;
		double H;

		public Sample() {
			X = (rand.nextDouble() - .5) * 131.2;
			Y = (rand.nextDouble() - .5) * 41;
			H = (rand.nextDouble() - .5) * 2. * Math.PI;
		}

		public Sample(double X,double Y,double H) {
			this.X = X;
			this.Y = Y;
			this.H = H;
		}

		public Sample motion(double speed,double turnrate) {
			double dist = speed * time;
			double theta = turnrate * time;
			// TODO: apply gaussian dist.
			// TODO: account for moving and turning together
			return new Sample(X * Math.cos(H+theta), Y * Math.sin(H+theta), H+theta);
		}

		// sonar reading probability
		// (adapted from mapping code.)
		public double probability(double sp[]) {
			double prob = 0.;

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

	}

}
