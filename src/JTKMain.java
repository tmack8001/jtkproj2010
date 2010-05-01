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
	}

}
