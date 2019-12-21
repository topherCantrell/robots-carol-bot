package driver;

/**
 * This interface describes constants and behaviors
 * of all robot bases.
 */
public interface MotorDriverInterface
{
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	
	public static final int FORWARD = 1;
	public static final int STOP = 0;
	public static final int REVERSE = 3;	

	/**
	 * This method sets the state of each motor.
	 * @param left the left motor value
	 * @param right the right motor value
 	 */		
	void setMotors(int left, int right);	
	
	/**
	 * This method stops both motors.
	 */
	void allStop();
	
	/**
	 * This method sets both motors to forward.
	 */
	void forward();

	/**
	 * This method sets the "direction" motor to STOP
	 * and the other motor to FORWARD.
	 * @param direction the direction to skew
	 */	
	void skew(int direction);
	
	/**
	 * This method sets the "direction" motor to REVERSE
	 * and the other motor to REVERSE
	 * @param direction the direction to spin
	 */
	void spin(int direction);
	
}
