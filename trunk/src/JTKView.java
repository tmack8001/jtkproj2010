/*
 * filename: JTKView.java
 * Trevor Mack -- May 7th
 */

import java.awt.*;
import java.awt.event.*;

import javaclient2.Position2DInterface;
import javaclient2.SonarInterface;

import javax.swing.*;


@SuppressWarnings("serial")
public
class JTKView extends JFrame {
    //... Components
    private JMenuBar menu;
    private GridMap gridMap;
    
    private JFrame sonarFrame;
    private SonarView sonarView; //this shows the current sonar reading (from James)
    //private JTKMapImage particleImage; //this shows the current state of the particles for localization
    
    
    private JButton sonarButton;
    private JButton exitButton;
    
    //data to use to update
    SonarInterface sonarData; //sonar model
    Position2DInterface robotData; //robot model
    
    //======================================================= constructor
    /** Constructor */
    public JTKView(int width, int height, double mpp) {
        //... Set up the logic
    	
        //... Initialize components
    	menu = new JMenuBar();
    	sonarButton = new JButton("Sonar Array");
    	exitButton = new JButton("Exit Program");
    
    	sonarFrame = new JFrame();
    	sonarView = new SonarView();
    	sonarFrame.setSize(200, 400);
    	sonarFrame.add(sonarView);
    	sonarFrame.setVisible(false);
    	
    	gridMap = new GridMap(width, height, mpp);
    	
        //... Layout the components.      
        menu.add(sonarButton);
    	menu.add(exitButton);
    	
    	JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(menu, BorderLayout.NORTH);
        content.add(gridMap, BorderLayout.SOUTH);
        
        sonarButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sonarFrame.setVisible(true);
			}
		});
        
        exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();	
				System.exit(0);
			}
		});
        
        //... finalize layout
        this.setContentPane(content);
        this.pack();
        this.setLocationRelativeTo(null);
        
        this.setTitle("SLAM - JTK");
        // The window closing event should probably be passed to the 
        // Controller in a real program, but this is a short example.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public void setModel(SonarInterface sonar, Position2DInterface motor) {
    	//update SonarPanel if visible
    	//update Map with sonar data
    	sonarData = sonar;
    	robotData = motor;
    	
    	sonarView.updateSonars(sonarData.getData().getRanges());
    	repaint();
    }
    
    public void repaint() {
    	gridMap.setVal(robotData.getX(), robotData.getY(), -1);
    	Mapper.estimateObstacle(gridMap, robotData, sonarData.getData().getRanges());
    	gridMap.repaint();
    	sonarFrame.repaint();
    }
    
    void showError(String errMessage) {
        JOptionPane.showMessageDialog(this, errMessage);
    }
    
    void addViewListener(ActionListener cal) {
        sonarButton.addActionListener(cal);
    }
}