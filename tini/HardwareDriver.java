import javax.comm.*;
import java.io.*;
import com.dalsemi.system.BitPort;

/**
 * This driver serves as abstraction interface to
 * the CarolBot hardware functions.
 *
 * There appears to be lots of glitches in my TINI board that I am attributing
 * to a static ZAP.
 * - The Ethernet TX never lights (and doesn't appear to ever transmit)
 * - Serial writes appear to go to BOTH ports, and you we must read from
 *   serial0 to get the serial1 data. Strange stuff in need of
 *   investigation.
 */
public class HardwareDriver
{

    // Serial commands to the STAMP board
    static byte [] corSetCommand = {17,0,0};
    static byte [] motorSetCommand = {'M',0,0};
    static byte [] sensorSetCommand = {'S',0};
    static byte [] playerSetCommand = {'P',0};

    public static final int SENSOR_FRONT = 1;
    public static final int SENSOR_LEFT = 2;
    public static final int SENSOR_RIGHT = 3;

    public static final int PLAYER_STOP = 0;
    public static final int PLAYER_PLAY = 2;
    public static final int PLAYER_FORWARD = 1;

    public static final int MOTOR_OFF = 0;
    public static final int MOTOR_FORWARD_HALF = 1;
    //public static final int MOTOR_FORWARD_FULL = 5;
    public static final int MOTOR_BACKWARD_HALF = 3;
    //public static final int MOTOR_BACKWARD_FULL = 7;

    // IO with the BASIC STAMP2
    OutputStream stampOutput;
    InputStream stampInput;

    // IO with the LCD
    //OutputStream lcdOutput;
    //InputStream lcdInput;

    // A bit port for visual proof-of-running
    BitPort bitPort;

    /**
     * This method connects the serial interfaces, initializes
     * the streams, and configures the LCD screen.
     * @throws Exception on errors making connections
     */
    public HardwareDriver() throws Exception
    {

        String stampPort = "serial1"; // The male port
        //String lcdPort = "serial0";   // The female port

        // Next time around, the board won't spew while starting up
        com.dalsemi.system.TINIOS.setSerialBootMessagesState(false);

        // Release the control lines for the BitPort TTL I/O
        com.dalsemi.system.TINIOS.setRTSCTSFlowControlEnable(0,false);
        //com.dalsemi.system.TINIOS.setRTSCTSFlowControlEnable(`,false);

        // serial-1 normally used in 1-Wire. Release that for general use ...
        // YOU MUST DISABLE THE 1-WIRE DRIVER BY GROUNDING THE EN2480 SIGNAL
        //   (PIN 26 OF THE SIMM CONNECTOR)
        //com.dalsemi.system.TINIOS.enableSerialPort1();

        // An activity light to prove we live!
        bitPort = new BitPort(BitPort.Port3Bit5);

        // Open communications to the BASIC STAMP
        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(stampPort);
        SerialPort serialPort = (SerialPort)portId.open("STAMP", 2000);
        serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,
        SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
        stampOutput = serialPort.getOutputStream();
        stampInput = serialPort.getInputStream();

        // Open communications to the LCD
        //CommPortIdentifier portId2 = CommPortIdentifier.getPortIdentifier(lcdPort);
        //SerialPort serialPort2 = (SerialPort)portId2.open("LCD", 2000);
        //serialPort2.setSerialPortParams(9600,SerialPort.DATABITS_8,
        //SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
        //lcdOutput = serialPort2.getOutputStream();
        //lcdInput = serialPort2.getInputStream();

        // Configure the LCD mode to no-cursor
        stampOutput.write(4);     // Hide cursor command

        // Clear the LCD screen
        printAt(0,0,"                    ");
        printAt(1,0,"                    ");
        printAt(2,0,"                    ");
        printAt(3,0,"                    ");

        // Splash screen from driver ... eventually we should "load" our own
        // splash screen into the LCD
        printAt(1,3,"2001 Carolbot");

        // Flash the LED to indicate the hardware driver is ready
        for(int x=0;x<5;++x) {
            bitPort.clear();
            Thread.sleep(100);
            bitPort.set();
            Thread.sleep(100);
        }

    }

    /**
     * This method halts the system with an error code by continually
     * blinking the TINI LED error-code number of times and then
     * pausing.
     *
     * Please reserve errCode<10 for this hardware driver.
     *
     * @param errCode the error code for the user
     */
    public void haltOnError(int errCode)
    {
        try {            
            while(true) {
                for(int x=0;x<errCode;++x) {
                    bitPort.set();
                    Thread.sleep(250);
                    bitPort.clear();
                    Thread.sleep(250);
                }
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            // Not much we can do about Thread.sleep exceptions
        }
    }

    /**
     * This Thread.sleep replacement handles the exception.
     * @param dur ms to sleep
     */
    public void pause(int dur)
    {
        try {
            Thread.sleep(dur);
        } catch (Exception e) {
            haltOnError(9);
        }
    }

    /**
     * This method sends an array of bytes to the
     * BASIC STAMP2 waiting for the echo ack.
     * @param b the byte array to send
     */
    protected void sendToStamp(byte [] b)
    {
        try {
            int p;
            for(int x=0;x<b.length;++x) {
                stampOutput.write(b[x]);
                //lcdOutput.write(b[x]);
                do {
                    p = stampInput.read();
                } while(p!=b[x]);
            }
        } catch (Exception e) {
            haltOnError(5);
        }
    }

    /**
     * This method sends a message to the LCD display.
     * @param message the message to print
     */
    public void print(String message)
    {
        try {
            stampOutput.write(message.getBytes());
        } catch (Exception e) {
            haltOnError(7);
        }
    }

    /**
     * This method moves the LCD cursor and then prints a
     * message.
     * @param row the row number
     * @param column the column number
     * @Param message the message to print
     */
    public void printAt(int row, int column, String message)
    {
        corSetCommand[1] = (byte)column;
        corSetCommand[2] = (byte)row;
        try {
            stampOutput.write(corSetCommand);
            stampOutput.write(message.getBytes());
        } catch (Exception e) {
            haltOnError(8);
        }
    }

    /**
     * This method negotiates with the STAMP2 and returns
     * the value from the specified sensor.
     * @param sensor the sensor to read
     * @return the sensor's value
     */
    public int readSensor(int sensor)
    {
        sensorSetCommand[1]=(byte)(sensor+'0');
        sendToStamp(sensorSetCommand);
        try {
            int a = stampInput.read()-'0';
            int b = stampInput.read()-'0';
            int c = stampInput.read()-'0';
            return a*100+b*10+c;
        } catch (Exception e) {
            haltOnError(6);
            return 0; // NEVER GETS HERE
        }

    }

    public void pressPlayerButton(int button)
    {
        playerSetCommand[1] = (byte)(button+'0');
        sendToStamp(playerSetCommand);
        // A slight pause here ... give the player time to
        // register the press
        if(button==2) pause(1000); // Play button is "slow"
        else pause(500);
    }

    public void setMotors(int left, int right)
    {
        motorSetCommand[1]=(byte)(left+'0');
        motorSetCommand[2]=(byte)(right+'0');
        sendToStamp(motorSetCommand);
    }


    public void doDiagnostics()
    {

        // Try the player PLAY and FORWARD to play song 6
        pressPlayerButton(PLAYER_PLAY);
        pressPlayerButton(PLAYER_FORWARD);
        pressPlayerButton(PLAYER_FORWARD);
        pressPlayerButton(PLAYER_FORWARD);
        pressPlayerButton(PLAYER_FORWARD);
        pressPlayerButton(PLAYER_FORWARD);
        pressPlayerButton(PLAYER_PLAY);

        // Try some basic motion commands making a small square
        setMotors(MOTOR_FORWARD_HALF,MOTOR_FORWARD_HALF);
        pause(4000);
        setMotors(MOTOR_FORWARD_HALF,MOTOR_BACKWARD_HALF);
        pause(500);
        setMotors(MOTOR_FORWARD_HALF,MOTOR_FORWARD_HALF);
        pause(4000);
        setMotors(MOTOR_FORWARD_HALF,MOTOR_BACKWARD_HALF);
        pause(500);
        setMotors(MOTOR_FORWARD_HALF,MOTOR_FORWARD_HALF);
        pause(4000);
        setMotors(MOTOR_FORWARD_HALF,MOTOR_BACKWARD_HALF);
        pause(500);
        setMotors(MOTOR_FORWARD_HALF,MOTOR_FORWARD_HALF);
        pause(4000);
        setMotors(MOTOR_FORWARD_HALF,MOTOR_BACKWARD_HALF);
        pause(500);
        setMotors(MOTOR_OFF,MOTOR_OFF);

        // Try all three sensors several times
        for(int x=0;x<5;++x) {
            int v = readSensor(SENSOR_FRONT);
            String g = " f"+v;
            print(g);
            pause(1000);
        }
        for(int x=0;x<5;++x) {
            int v = readSensor(SENSOR_RIGHT);
            String g = " r"+v;
            print(g);
            pause(1000);
        }
        for(int x=0;x<5;++x) {
            int v = readSensor(SENSOR_LEFT);
            String g = " l"+v;
            print(g);
            pause(1000);
        }

        // Everything off now
        pressPlayerButton(PLAYER_STOP);
        pressPlayerButton(PLAYER_STOP);

    }    

}