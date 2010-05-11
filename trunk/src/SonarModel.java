
public class SonarModel {
	private double d1 = .1;
	private double d2 = .2;
	private double d3 = .3;
	private double confidence = .16;
	private int w;
	private double s_table[][]; 
	public static void main(String argv[]) {
		SonarModel m = new SonarModel();
		System.out.println(m.s(2.,0.));
	}

	public SonarModel() {
		this(100);
	}

	public SonarModel(int w) {
		this.w = w;
		s_table = new double[w][w];
		buildtables();
	}
	
	private void buildtables() {
		for(int i=0;i<w;i++) 
			for(int j=0;j<w;j++)
			    s_table[i][j] = _s(((double)i/(double)w) * 3.,
					     ((double)j/(double)w) * .3 - .15);
		
	}
	public SonarModel(double d1,double d2,double d3) {
		this();
		this.d1 = d1;
		this.d2 = d2;
		this.d3 = d3;
	}
	public double N(double mean,double stddev,double theta) {
		return (1/(stddev * Math.sqrt(2*Math.PI))) * 
			Math.exp(-(Math.pow(theta-mean,2.0))/
				(2.0 * Math.pow(stddev,2.0)));
	}
	public double g(double y) {
		double result = (-0.05/3.25)*y + 0.05;
		//return (result > 0.0) ? result : 0.0;
		return result;
	}
	public double s(double y,double theta) {
		int i = (int)((y / 3.) * (double)w);
		int j = (int)((theta + .15) / .3 * (double)w);
		if(i >= w || i < 0 || j >= w || j < 0) return 0.;
		return s_table[i][j];
	}
	public double _s(double y,double theta) {
		return g(y) * N(0.0,0.05,theta);
	}
	public double PM_d_theta_old(double d,double x) {
		if(x <= d-d1) 
			return .5-confidence;
		if(x >= d+d3)
			return .5;
		if(x <= d+d2 && x >= d+d1) 
			return .5+confidence;
		if(x < d+d1 && x > d-d1)
			return (confidence/d1)*(x-d) + .5;
		if(x > d+d2 && x < d+d3)
			return (confidence/(d2-d3))*(x-(d+d3)) + .5;
		throw new RuntimeException("PM_d_theta undefined at: " + x);
	}
	public double PM_d_theta(double y,double theta,double d) {
		//double prior = PM_d_theta_prior(d,x);
		double prior = .5;
		double s = 0.;
		if(d < y-d1)
			s = -s(y,theta);
		else if(d < y+d1)
			s = -s(y,theta) + (s(y,theta) / d1)*(d-y+d1);
		else if(d < y+d2)
			s = s(y,theta);
		else if(d < y+d3)
			s = s(y,theta) - (s(y,theta) / (d3-d2))*(d-y-d2);
		return prior + s;
	}
}
