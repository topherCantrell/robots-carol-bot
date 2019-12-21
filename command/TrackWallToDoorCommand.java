package command;

import driver.*;
import java.util.*;

public class TrackWallToDoorCommand implements Executable
{
		
	private SensorDriverInterface sensors;
	private MotorDriverInterface motors;
	
	private int wallID;
	private int wallSensor;
	
	protected int getDoorDriftTime() {return 100;}
	protected int getSkewTime() {return 50;}
	protected int getForwardDriftTime() {return 50;}
	
	protected int getDoorThreshold() {return 60;}
	protected int getWallTooFar() {return 120;}
	protected int getWallTooNear() {return 80;}
	
	public TrackWallToDoorCommand(int insideMotorID,
		MotorDriverInterface motors, 
		SensorDriverInterface sensors)
	{		
		this.wallID = insideMotorID;
		this.motors = motors;
		this.sensors = sensors;
		if(wallID == MotorDriverInterface.LEFT) {
			wallSensor = SensorDriverInterface.LEFT;
		} else {
			wallSensor = SensorDriverInterface.RIGHT;
		}
	}
	
	public static void main(String [] args) throws Exception
	{
		
		MotorDriverInterface motors = null;
		SensorDriverInterface sensors = null;
		MP3PlayerInterface mp3 = null;
		
		// Obect to track wall on left to next door
		Executable toOffice = new TrackWallToDoorCommand(
			MotorDriverInterface.LEFT, motors,sensors);
			
		// Object to play next song for 60 seconds
		Executable doPlay = new PlaySongCommand(0,60,true,mp3);
		
		// Object to track door on left to next wall
		Executable toWall = new TrackDoorToWallCommand(
			MotorDriverInterface.LEFT, motors, sensors);
		
		// Build the sub-list of things to do for one office
		List list = new ArrayList();
		list.add(toOffice);
		list.add(doPlay);
		list.add(toWall);		
		ExecutableList subList = new ExecutableList();
		subList.setCommandList(list);
		
		// Build a list that executes the sub-list 10 times
		// for ten separate offices along a left wall.		
		list = new ArrayList();
		for(int x=0;x<10;++x) {
			list.add(subList);
		}		
		ExecutableList masterList = new ExecutableList();
		masterList.setCommandList(list);
		
		// Run the master list
		masterList.execute();
		
	}
	
	public void execute() throws Exception
	{				
		
		motors.forward();
		
		while(true) {
			int s = sensors.getSensorValue(wallSensor);
			
			// If we found the door, drift a bit and stop
			if(s<getDoorThreshold()) {				
				Thread.sleep(getDoorDriftTime());
				motors.allStop();
				return;
			}
			
			// If we are too close to the wall, skew outward
			if(s<getWallTooNear()) { 				
				if(wallID == MotorDriverInterface.LEFT) {
					motors.skew(MotorDriverInterface.RIGHT);								
				} else {
					motors.skew(MotorDriverInterface.LEFT);
				}
				Thread.sleep(getSkewTime());						
			} 
			
			// If we are too close to the wall, skew inward
			else if(s>getWallTooFar()) {
				if(wallID == MotorDriverInterface.LEFT) {
					motors.skew(MotorDriverInterface.LEFT);
				} else {
					motors.skew(MotorDriverInterface.RIGHT);
				}
				Thread.sleep(getSkewTime());	
			} 
			
			// Wall distance is OK ... drift forward
			else {
				Thread.sleep(getForwardDriftTime());
			}			
			
		}
		
	}	
	
}
