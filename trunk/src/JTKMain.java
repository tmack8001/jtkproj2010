import javaclient2.PlayerClient;
import javaclient2.Position2DInterface;
import javaclient2.SonarInterface;
import javaclient2.structures.PlayerConstants;
import javaclient2.structures.sonar.PlayerSonarData;

public class JTKMain {

	/**
	 * @param args
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
		
		while (true) {
			float turnRate, speed;
			
			// read all the data
			robot.readAll();
			
			// don't do anything unless there's data
			
			if (sonar.isDataReady()) {
				PlayerSonarData sonarData = sonar.getData();
				float[] ranges = sonarData.getRanges();
				
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
