
import waba.ui.*;
import waba.fx.*;
import waba.io.*;


public class Carol extends MainWindow
{

  Button leftFor, leftStop, leftBack, rightFor, rightStop, rightBack;
  Button playMP3, stopMP3, forwardMP3;  

  Button snsFront, snsRight, snsLeft;
  Label  laFront, laRight, laLeft;

  Button closeButton;

  SerialPort serialPort;
  byte [] buf;
  byte leftMotor = '0';
  byte rightMotor = '0';

  public Carol()
  {

        serialPort = new SerialPort(0,9600);
        buf = new byte[3];

	Title title = new Title("CarolBot Diagnostics");
	title.setRect(0, 0, this.width, 15);
	add(title);	

        leftFor = new Button("F");
        leftFor.setRect(20, 30, 15, 15);
        add(leftFor);
        leftStop = new Button(" ");
        leftStop.setRect(20, 50, 15, 15);
        add(leftStop);
        leftBack = new Button("B");
        leftBack.setRect(20,70, 15, 15);
        add(leftBack);

	rightFor = new Button("F");
        rightFor.setRect(130, 30, 15, 15);
        add(rightFor);
        rightStop = new Button(" ");
        rightStop.setRect(130, 50, 15, 15);
        add(rightStop);
        rightBack = new Button("B");
        rightBack.setRect(130, 70, 15, 15);
        add(rightBack);

        playMP3 = new Button("Play");
        playMP3.setRect(60,30,44,15);
        add(playMP3);
        stopMP3 = new Button("Stop");
        stopMP3.setRect(60,50,44,15);
        add(stopMP3);        
        forwardMP3 = new Button("Next");
        forwardMP3.setRect(60,70,44,15);
        add(forwardMP3);

        snsFront = new Button("Front");
        snsFront.setRect(20,90,44,15);
        add(snsFront);
        snsLeft = new Button("Left");
        snsLeft.setRect(20,106,44,15);
        add(snsLeft);
        snsRight = new Button("Right");
        snsRight.setRect(20,122,44,15);
        add(snsRight);        

        laFront = new Label("");
        laFront.setRect(70,90,44,15);
        add(laFront);
        laLeft = new Label("");
        laLeft.setRect(70,106,44,15);
        add(laLeft);
        laRight = new Label("");
        laRight.setRect(70,122,44,15);
        add(laRight);

	closeButton = new Button("Close");
	closeButton.setRect(0, this.height - 15, 44, 15);
	add(closeButton);

        Sound.tone(2500, 100);
	Sound.tone(500, 150);
	Sound.tone(2500, 100);

	
  }

  private void sendCommand(byte [] buf,int start,int len)
  {
    byte[] readIt = new byte[1];
    for(int x=0;x<len;++x) {
      serialPort.writeBytes(buf,start+x,1);
      do {
        serialPort.readBytes(readIt,0,1);
      } while(readIt[0]!=buf[start+x]);      
    }
  }

  private String readSensor(int s)
  {
    buf[0] = 'S';
    buf[1] = (byte)('0'+s);
    sendCommand(buf,0,2);
    serialPort.readBytes(buf,0,3);
    int ret = (buf[0]-'0')*100 + (buf[1]-'0')*10 +(buf[2]-'0');
    StringBuffer sb = new StringBuffer();
    sb.append(ret);
    return sb.toString();    
  }

  private void updateMotors()
  {    
    buf[0] = 'M';
    buf[1] = leftMotor;
    buf[2] = rightMotor;
    sendCommand(buf,0,3);    
  }

  private void pressButton(int bn)
  {
    buf[0] = 'P';
    buf[1] = (byte)('0'+bn); 
    sendCommand(buf,0,2);
  }

  public void onEvent(Event event)
  {
	if (event.type == ControlEvent.PRESSED)
		{

		  if (event.target == closeButton) {
			exit(0);	
  		  } else if(event.target == leftFor) {
                    leftMotor = '1';
                    updateMotors();
                  } else if(event.target == leftStop) {
                    leftMotor = '0';
                    updateMotors();
                  } else if(event.target == leftBack) {
                    leftMotor = '3';
                    updateMotors();
                  } else if(event.target == rightFor) {
                    rightMotor = '1';
                    updateMotors();
                  } else if(event.target == rightStop) {
                    rightMotor = '0';
                    updateMotors();
                  } else if(event.target == rightBack) {
                    rightMotor = '3';
                    updateMotors();
                  }

                  else if(event.target == playMP3) {
                    pressButton(2);
                  } else if(event.target == stopMP3) {
                    pressButton(0);
                  } else if(event.target == forwardMP3) {
                    pressButton(1);
                  }

                  else if(event.target == snsFront) {
                    String s = readSensor(1);
                    laFront.setText(s);
                  } else if(event.target == snsLeft) {
                    String s = readSensor(2);
                    laLeft.setText(s);
                  } else if(event.target == snsRight) {
                    String s = readSensor(3);
                    laRight.setText(s);
                  }

        }

  }
}