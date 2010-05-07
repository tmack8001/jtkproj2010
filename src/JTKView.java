/*
 * filename: JTKView.java
 * Trevor Mack -- May 7th
 */

import homework5.GridMap;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
class JTKView extends JFrame {
    //... Components
    private JMenuBar menu;
    private JPanel gridMap;
    private JButton sonarButton;
    
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