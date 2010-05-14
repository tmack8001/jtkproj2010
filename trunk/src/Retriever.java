/**
 * filename: Retriever.java
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;


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
public class Retriever {
	
	//... Components
	private static JFrame f;
	
    private static JMenuBar menu;
    //private static GridMap gridMap;
    
    private static JFrame sonarFrame;
    private static SonarView sonarView; //this shows the current sonar reading (from James)
    //private static JTKMapImage particleImage; //this shows the current state of the particles for localization
    
    private static JButton sonarButton;
    private static JButton exitButton;
	
	/**
	 * @param 	args[0] host
	 * 			args[1] port
	 * 			args[2] filename // point file for path planning
	 */
	public static void main(String[] args) {
		int port = 6665;
		String server = "localhost";
		List<Point2D> points = new LinkedList<Point2D>();
		
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
		
		//create GUI
		f = new JFrame("JTKProj2010 - Retriever");
		JTKMap map = new JTKMap();
		JTKMapImage jtk = new JTKMapImage(map);
		
		//... Initialize menu
    	menu = new JMenuBar();
    	sonarButton = new JButton("Sonar Array");
    	exitButton = new JButton("Exit Program");
    
    	sonarFrame = new JFrame();
    	sonarView = new SonarView();
    	sonarFrame.setSize(200, 400);
    	sonarFrame.add(sonarView);
    	sonarFrame.setVisible(false);
    	
    	menu.add(sonarButton);
    	menu.add(exitButton);
    	
    	sonarButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sonarFrame.setVisible(true);
			}
		});
        
        exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				f.setVisible(false);
				f.dispose();	
				System.exit(0);
			}
		});
    	
		//add Components
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(menu, BorderLayout.NORTH);
        content.add(jtk, BorderLayout.CENTER);
		
        f.add(content);
        
		f.pack();
		f.setVisible(true);
		
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//add waypoints to the map
		jtk.setPoints(points);
		
		SonarModel sonarModel = new SonarModel(10);
		JTKLocal local = new JTKLocal(sonarModel, 10000, map);
		
		//add particles to the map
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
				
				//gather data
				PlayerSonarData sonarData = sonar.getData();
				float[] ranges = sonarData.getRanges();
				
				//update map
				if(sonarFrame.isVisible()) {
					sonarView.updateSonars(ranges);
					sonarFrame.repaint();
				}
				jtk.repaint();
				
				if (ranges.length == 0)
					continue;

				for(int i=0;i<8;i++) 
					sp[i] = ranges[i];
                
				//execute a random walk
				double left = sp[0] + sp[1] + sp[2] + sp[3];
				double right = sp[4] + sp[5] + sp[6] + sp[7];
				if(sp[3] + sp[4] > 2f) {
					turnRate = (float)(Math.sqrt(left) - Math.sqrt(right));
					speed = .5f;
				} else {
					speed = 0f;
					turnRate = (float)Math.PI/12f;
				}
				
				// send the command
				motor.setSpeed(speed, turnRate);
				//update particles with given directions
				local.update((double)speed,(double)turnRate,sp);
			}
		}					
	}
	
}
