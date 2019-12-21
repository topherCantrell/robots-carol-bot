public class Brain
{

    HardwareDriver driver;

    public Brain(HardwareDriver driver)
    {
        this.driver = driver;
    }

    public void navigateDoorway(int sensorNumber)
    {
        // Keep going until we find the wall again.
        // After so long, we need to assume we drifted to right
        // and turn to acquire
    }

    public void trackWallToDoor(int sensorNumber)
    {

        // TARGET 150 ... correct at 120 and 180
        // DOOR=BELOW 80

        // Establish jerk in and jerk out depending on our sensor
        int jerkIn_LeftMotorValue=0;
        int jerkOut_LeftMotorValue=0;
        int jerkOut_RightMotorValue=0;
        int jerkIn_RightMotorValue=0;
        if(sensorNumber==HardwareDriver.SENSOR_LEFT) {
            jerkIn_LeftMotorValue=HardwareDriver.MOTOR_OFF;
            jerkIn_RightMotorValue=HardwareDriver.MOTOR_FORWARD_HALF;
            jerkOut_LeftMotorValue=HardwareDriver.MOTOR_FORWARD_HALF;
            jerkOut_RightMotorValue=HardwareDriver.MOTOR_OFF;
        } else {
            jerkIn_LeftMotorValue=HardwareDriver.MOTOR_FORWARD_HALF;
            jerkIn_RightMotorValue=HardwareDriver.MOTOR_OFF;
            jerkOut_LeftMotorValue=HardwareDriver.MOTOR_OFF;
            jerkOut_RightMotorValue=HardwareDriver.MOTOR_FORWARD_HALF;
        }

        // Start rolling
        driver.setMotors(HardwareDriver.MOTOR_FORWARD_HALF,
        HardwareDriver.MOTOR_FORWARD_HALF);

        // Track until we find a door
        while(true) {

            // Look at the wall
            int sens = driver.readSensor(sensorNumber);

            // Return if we lost it completely (must be a doorway)
            if(sens<80) {
                // TOPHER PAUSE TO DRIFT INTO DOORWAY MORE
                // Turn motors off
                driver.setMotors(HardwareDriver.MOTOR_OFF,
                HardwareDriver.MOTOR_OFF);
                return;
            }

            // Bounce between 180 and 120 (center on 150)
            if(sens>180) { // Getting too close to wall
                driver.setMotors(jerkOut_LeftMotorValue,jerkOut_RightMotorValue);
                driver.pause(100);
                driver.setMotors(HardwareDriver.MOTOR_FORWARD_HALF,
                HardwareDriver.MOTOR_FORWARD_HALF);
            } else if(sens<120) { // Getting too far from wall
                driver.setMotors(jerkIn_LeftMotorValue,jerkIn_RightMotorValue);
                driver.pause(100);
                driver.setMotors(HardwareDriver.MOTOR_FORWARD_HALF,
                HardwareDriver.MOTOR_FORWARD_HALF);
            }

            driver.pause(1000); // Allow corrections to take effect

        }

    }

    public static void main(String [] args)
    {

        HardwareDriver d = null;
        try {

            d = new HardwareDriver();
            Brain brain = new Brain(d);

            brain.trackWallToDoor(HardwareDriver.SENSOR_LEFT);

        } catch (Exception e) {
            d.haltOnError(15);
        }

    }
}
