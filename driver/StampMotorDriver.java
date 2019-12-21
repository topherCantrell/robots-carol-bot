package driver;

import waba.io.*;

/**
 * This class implements the MotorDriverInterface by communicating
 * with a BASIC stamp.
 */
public class StampMotorDriver implements MotorDriverInterface
{
	
	private BASICStampHandler handler;	
	private byte [] b = new byte[1];
	
	/**
	 * This constructs a new StampMotorDriver.
	 * @param handler the concrete BASICStampHandler
	 */
	public StampMotorDriver(BASICStampHandler handler)
	{
		this.handler = handler;
	}
	
	// From MotorDriverInterface
	
	public void setMotors(int left, int right)
	{		
		b[0] = (byte)(left*16 + right);
		handler.sendCommand(b,0,1);
	}
	
	public void allStop()
	{
		setMotors(MotorDriverInterface.STOP,
			MotorDriverInterface.STOP);
		
	}
	
	public void forward()
	{
		setMotors(MotorDriverInterface.FORWARD,
			MotorDriverInterface.FORWARD);
	}
	
	public void skew(int direction)
	{
		if(direction==MotorDriverInterface.LEFT) {
			setMotors(MotorDriverInterface.STOP,
				MotorDriverInterface.FORWARD);
		} else {
			setMotors(MotorDriverInterface.FORWARD,
				MotorDriverInterface.STOP);
		}
	}
	
	public void spin(int direction)
	{
		if(direction==MotorDriverInterface.LEFT) {
			setMotors(MotorDriverInterface.REVERSE,
				MotorDriverInterface.REVERSE);
		} else {
			setMotors(MotorDriverInterface.REVERSE,
				MotorDriverInterface.STOP);
		}
	}
	
}
