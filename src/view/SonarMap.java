package view;

import javaclient2.SonarInterface;

import javax.swing.JFrame;

public class SonarMap extends JFrame {

	//data to use to update
    SonarInterface sonarData; //sonar model
	
	SonarMap(SonarInterface sonars) {
		sonarData = sonars;
	}
}
