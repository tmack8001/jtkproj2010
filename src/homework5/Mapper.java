package homework5;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.*;

import javaclient2.PlayerClient;
import javaclient2.Position2DInterface;
import javaclient2.SonarInterface;
import javaclient2.structures.PlayerConstants;
import javaclient2.structures.sonar.PlayerSonarData;


/**
 * Simple hello-world style Player/Stage JavaClient example.
 *
 * @author Gregory Von Pless
 */
public class Mapper {

	final static int OBSTACLE = 0;
	final static int OPEN = 255;
	final static int[] SONARS = { 90, 50, 30, 10, -10, -30, -50, -90 };
	
	/**
	 * Read and print sonars, avoid obstacles.
	 * 
	 * @param args args[0] = server name
	 * 			   args[1] = robot port
	 * 			   these default to localhost and 6665
	 */
	public static void main(String[] args) {
		int port = 6665;
		String server = "localhost";
		
		if (args.length == 2) {
			server = args[0];
			port = Integer.parseInt(args[1]);
		}
		
		PlayerClient robot = new PlayerClient(server, port);
		SonarInterface sonar = robot.requestInterfaceSonar(0, 
				PlayerConstants.PLAYER_OPEN_MODE);
		Position2DInterface motor = robot.requestInterfacePosition2D(0, 
				PlayerConstants.PLAYER_OPEN_MODE);
		
		// turn stuff on.  this might not be necessary
		sonar.setSonarPower(1);
		motor.setMotorPower(1);
		
		JFrame frame = new JFrame();
		GridMap gridMap = new GridMap(50, 50, 0.0625);
		
		frame.add(gridMap);
		frame.setSize(new Dimension(800, 800));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		while (true) {
			float turnRate, speed;
			
			// read all the data
			robot.readAll();
			
			// don't do anything unless there's data
			if (sonar.isDataReady()) {
				PlayerSonarData sonarData = sonar.getData();
				float[] ranges = sonarData.getRanges();
				
				// for some reason the first read gets nothing...
				if (ranges.length == 0)
					continue;
				
				// print the sonars
				/*System.out.printf("[ ");
				for (float f : ranges)
					System.out.printf("%.2f ", f);
				System.out.println("]");
				*/
	
                // note different units for angle in
                // these two accessors!
                /*System.out.println(motor.getX() + " " +
                                   motor.getY() + " " +
                                   motor.getYaw());

               /* System.out.println(motor.getData().getPos().getPx() + " " +
                                   motor.getData().getPos().getPy() + " " +
                                   motor.getData().getPos().getPa());
                */
                double x = motor.getX();
                double y = motor.getY();
                double theta = motor.getYaw();
                
                gridMap.setVal(x, y, -1);
                estimateObstacle(gridMap, motor, ranges);
                /*for (int i = 0; i < ranges.length && i<8; i++) {
                	double range = ranges[i];
                	double sonarAngle = Math.toRadians( theta + SONARS[i] ); 
                	if(range < 2.4) {
                		double obx = x+range*Math.cos( sonarAngle );
                		double oby = y+range*Math.sin( sonarAngle );
                		int confidence = (255 - (int)(gridMap.getVal(obx, oby) * 1.1));
                		if(confidence < OBSTACLE)
                			confidence = OBSTACLE;
                		//System.out.println(confidence);
                		estimateObstacle( gridMap, obx, oby, confidence );
                	}
                }*/
                
				// do simple obstacle avoidance
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
				gridMap.repaint();
			}
		}
	}
	
	public static void estimateObstacle( GridMap gridMap, Position2DInterface motor, float[] ranges ) {
		double x = motor.getX();
        double y = motor.getY();
        double theta = motor.getYaw();
        
        for (int i = 0; i < ranges.length && i<8; i++) {
        	float range = ranges[i];
        	double sonarAngle = Math.toRadians( theta + SONARS[i] );
    		double obx = x+range*Math.cos( sonarAngle );
    		double oby = y+range*Math.sin( sonarAngle );
    		int confidence = (255 - (int)(gridMap.getVal(obx, oby) * 1.1));

    		if(confidence < OBSTACLE)
    			confidence = OBSTACLE;
    		if( range < 2.4 )
    			gridMap.setVal( obx, oby, confidence);
    		
    		//set everything in front of range as clear
    		for (float delta=0.1f; delta < range && range < 2.4f; delta += 0.1f) {
    			float range2 = range - delta;
    			obx = x+range2*Math.cos( sonarAngle );
        		oby = y+range2*Math.sin( sonarAngle );
        		confidence = (int)(gridMap.getVal(obx, oby) * 0.9);

        		if(confidence < OPEN)
        			confidence = OPEN;
        		
        		if( gridMap.getVal(obx, oby) != -1)
        			gridMap.setVal( obx, oby, confidence);
    		}
        }
	}

}
