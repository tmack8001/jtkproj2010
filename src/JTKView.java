/*
 * filename: JTKView.java
 * Trevor Mack -- May 7th
 */

import homework5.GridMap;
import homework5.Mapper;

import java.awt.*;
import java.awt.event.*;

import javaclient2.Position2DInterface;
import javaclient2.SonarInterface;

import javax.swing.*;

@SuppressWarnings("serial")
class JTKView extends JFrame {
    //... Components
    private JMenuBar menu;
    private GridMap gridMap;
    private JFrame sonarMap; //this shows the current sonar reading (from James)
    
    private JButton sonarButton;
    private JButton exitButton;
    
    //data to use to update
    SonarInterface sonarData; //sonar model
    Position2DInterface robotData; //robot model
    
    //======================================================= constructor
    /** Constructor */
    JTKView(int width, int height, double mpp) {
        //... Set up the logic
    	
        //... Initialize components
    	menu = new JMenuBar();
    	sonarButton = new JButton("Sonar Array");
    	exitButton = new JButton("Exit Program");
    
    	sonarMap = new JFrame();
    	sonarMap.setVisible(false);
    	
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
				sonarMap.setVisible(true);
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
        
        this.setTitle("SLAM - JTK");
        // The window closing event should probably be passed to the 
        // Controller in a real program, but this is a short example.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    void setModel(SonarInterface sonar, Position2DInterface motor) {
    	//update SonarPanel if visible
    	//update Map with sonar data
    	sonarData = sonar;
    	robotData = motor;
    }
    
    public void repaint() {
    	gridMap.setVal(robotData.getX(), robotData.getY(), -1);
    	Mapper.estimateObstacle(gridMap, robotData, sonarData.getData().getRanges());
    	this.gridMap.repaint();
    	
    	if(sonarMap.isVisible()) {
    		sonarMap.repaint();
    	}
    }
    
    void showError(String errMessage) {
        JOptionPane.showMessageDialog(this, errMessage);
    }
    
    void addViewListener(ActionListener cal) {
        sonarButton.addActionListener(cal);
    }
}