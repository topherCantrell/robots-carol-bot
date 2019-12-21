
import waba.ui.*;
import waba.fx.*;
import waba.io.*;
import waba.sys.*;

import driver.*;
import superwabadriver.*;

public class CarolBotRC extends MainWindow
{

    MotorDriverInterface motorDriver;
    Button bForward;    
    Button bStop;
    Button bSkewLeft;
    Button bSkewRight;
    Button bSpinLeft;
    Button bSpinRight;

    public CarolBotRC()
    {

        super("Carol Bot Remote",TAB_ONLY_BORDER);

        SerialPort serialPort = new SerialPort(0,9600);
        BASICStampHandler handler = new SWBASICStampHandler(serialPort);
        
        motorDriver = new StampMotorDriver(handler);
        
        bForward = new Button("Forward");
        add(bForward);
        bForward.setRect(10,AFTER,PREFERRED,PREFERRED);
        
        bStop = new Button("Stop");
        add(bStop);
        bStop.setRect(AFTER+4,SAME,PREFERRED,PREFERRED);
        
    }
    
    public void onEvent(Event event)
    {
    	if(event.type == ControlEvent.PRESSED) {
    		if(event.target == bStop) {
    			motorDriver.allStop();
    		} else if(event.target== bForward) {
    			motorDriver.forward();
    		} else if(event.target == bSkewLeft) {
    			motorDriver.skew(MotorDriverInterface.LEFT);
    		} else if(event.target == bSkewRight) {
    			motorDriver.skew(MotorDriverInterface.RIGHT);
    		} else if(event.target == bSpinLeft) {
    			motorDriver.spin(MotorDriverInterface.LEFT);
    		} else if(event.target == bSpinRight) {
    			motorDriver.spin(MotorDriverInterface.RIGHT);
    		}
    	} 
    }

}