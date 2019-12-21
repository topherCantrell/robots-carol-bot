package driver;

public interface SensorDriverInterface
{
	
	public static final int LEFT = 0;
	public static final int FRONT = 1;
	public static final int RIGHT = 2;
	public static final int BACK = 3;
	
	int getSensorValue(int sensor);
	
	int [] getSensorValues();
}
      �