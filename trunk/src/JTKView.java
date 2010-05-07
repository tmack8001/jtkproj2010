// /CalcView.java - View component
//    Presentation only.  No user actions.
// Fred Swartz -- December 2004

import homework5.GridMap;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
class JTKView extends JFrame {
    //... Constants
    private static final String INITIAL_VALUE = "1";
    
    //... Components
    private JMenuBar menu;
    private JFrame gridMap;
    private JButton sonarButton;
    
    private BufferedImage theMap;
    private int imwidth, imheight;
    private double scale;
    
    //======================================================= constructor
    /** Constructor */
    JTKView(int width, int height, double mpp) {
        //... Set up the logic
    	
        //... Initialize components
    	menu = new JMenuBar();
    	sonarButton = new JButton("Sonar Array");
    	gridMap = new GridMap(width, height, mpp);
    	
        //... Layout the components.      
        menu.add(sonarButton);
    	
    	JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(menu, BorderLayout.NORTH);
        content.add(gridMap, BorderLayout.SOUTH);
        
        //... finalize layout
        this.setContentPane(content);
        this.pack();
        
        this.setTitle("Simple Calc - MVC");
        // The window closing event should probably be passed to the 
        // Controller in a real program, but this is a short example.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    void showError(String errMessage) {
        JOptionPane.showMessageDialog(this, errMessage);
    }
    
    void addViewListener(ActionListener cal) {
        sonarButton.addActionListener(cal);
    }
}