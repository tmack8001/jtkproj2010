/**
 * filename: Retriever.java
 */

import view.JTKView;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javaclient2.PlayerClient;
import javaclient2.Position2DInterface;
import javaclient2.SonarInterface;
import javaclient2.structures.PlayerConstants;
import javaclient2.structures.sonar.PlayerSonarData;

/**
 * This file is the main program that will be run when the project is finally turned in.
 * 
 * @author Trevor Mack - tmm9274
 * @author James Loomis - jtl2011
 * @author Kevin Hockey - kdh7733
 *
 */
public class JTKMain {

	//points the robot needs to get to
	static ArrayList<Point2D> points = new ArrayList<Point2D>(1);
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port = 6665;
		String server = "localhost";
		
		if (args.length == 2) {
			server = args[0];
			port = Integer.parseInt(args[1]);
		}else if(args.length == 3){
			server = args[0];
			port = Integer.parseInt(args[1]);
			String filename = args[2];
			File inFile  = new File(filename);
			
			try {
				BufferedReader reader = 
					new BufferedReader(new FileReader(inFile));
		        String line = null;
		        while ( (line=reader.readLine()) != null) {
		        	String[] temp = line.split(" ");
		            Point2D curPoint = new Point2D.Double();
		            curPoint.setLocation(Double.parseDouble(temp[0]), 
		            		Double.parseDouble(temp[1]));
		            points.add(curPoint);
		        }
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("FileNotFound thrown");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("IOException thrown");
			}	
		}
		
		//test print of arraylist points
		for(int x = 0; x < points.size(); x++){
			System.out.println(points.get(x).toString());
		}
		
		PlayerClient robot = new PlayerClient(server, port);
		SonarInterface sonar = robot.requestInterfaceSonar(0, 
				PlayerConstants.PLAYER_OPEN_MODE);
		Position2DInterface motor = robot.requestInterfacePosition2D(0, 
				PlayerConstants.PLAYER_OPEN_MODE);
		
		JTKView view = new JTKView(50, 50, 0.0625);
		view.setModel(sonar, motor);
		view.setVisible(true);
		
		// turn stuff on.  this might not be necessary
		sonar.setSonarPower(1);
		motor.setMotorPower(1);
		
		while (true) {
			float turnRate, speed;
			
			// read all the data
			robot.readAll();

			// don't do anything unless there's data
			
			if (sonar.isDataReady()) {
				PlayerSonarData sonarData = sonar.getData();
				float[] ranges = sonarData.getRanges();
				view.repaint();
				
				if (ranges.length == 0)
					continue;
                double x = motor.getX();
                double y = motor.getY();
                double theta = motor.getYaw();
                
				if (ranges[0] + ranges[1] < ranges[6] + ranges[7])
					turnRate = -20.0f * (float)Math.PI / 180.0f;
				else
					turnRate = 20.0f * (float)Math.PI / 180.0f;
				
				if (ranges[3] < 0.5f)
					speed = 0.0f;
				else
					speed = 0.1f;
				
				// send the command
				motor.setSpeed(speed, turnRate);
			}
		}
				
	}

}
