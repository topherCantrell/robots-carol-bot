package command;

import driver.*;

public class TrackDoorToWallCommand implements Executable
{
	
	private SensorDriverInterface sensors;
	private MotorDriverInterface motors; 
	
	int wallID;
	int wallSensor;
	
	protected int getDriftTime() {return 10;}	
	
	public TrackDoorToWallCommand(int insideMotorID,
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
	
	public void execute() throws Exception
	{		
		motors.forward();		
		Thread.sleep(getDriftTime());
	}
	
}