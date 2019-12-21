package waba;

import waba.ui.*;
import waba.fx.*;
import waba.io.*;
import waba.sys.*;

public class Carol extends MainWindow
{

  Button adjLeft, adjRight, allForward, spinLeft, spinRight, allStop;
  Button playMP3, stopMP3, forwardMP3;  

  Button lcd;
  Edit lcdEdit;

 // double d=1.2;

  Button snsFront, snsRight, snsLeft;
  Label  laFront, laRight, laLeft;

  Button closeButton;

  SerialPort serialPort;
  byte [] buf;
  byte leftMotor = 0;
  byte rightMotor = 0;

  public Carol()
  {

        serialPort = new SerialPort(0,9600);
        buf = new byte[1];

	Title title = new Title("CarolBot Diagnosticsss ");
	title.setRect(0, 0, this.width, 15);
	add(title);	

        adjLeft = new Button("<");
        adjLeft.setRect(50, 20, 15, 15);
        add(adjLeft);
        allForward = new Button("^");
        allForward.setRect(70, 20, 15, 15);
        add(allForward);
        adjRight = new Button(">");
        adjRight.setRect(90, 20, 15, 15);
        add(adjRight);

	spinLeft = new Button("S");
        spinLeft.setRect(50, 37, 15, 15);
        add(spinLeft);
        spinRight = new Button("S");
        spinRight.setRect(90, 37, 15, 15);
        add(spinRight);
        allStop = new Button("O");
        allStop.setRect(70, 37, 15, 15);
        add(allStop);

        playMP3 = new Button("Play");
        playMP3.setRect(20,55,35,15);
        add(playMP3);
        stopMP3 = new Button("Stop");
        stopMP3.setRect(60,55,35,15);
        add(stopMP3);        
        forwardMP3 = new Button("Next");
        forwardMP3.setRect(100,55,35,15);
        add(forwardMP3);

        lcd = new Button("LCD");
        lcd.setRect(20,75,25,15);
        add(lcd);

        lcdEdit = new Edit();
        lcdEdit.setRect(50,75,100,15);
        add(lcdEdit);


        snsFront = new Button("Front");
        snsFront.setRect(20,95,44,15);
        add(snsFront);
        snsLeft = new Button("Left");
        snsLeft.setRect(20,111,44,15);
        add(snsLeft);
        snsRight = new Button("Right");
        snsRight.setRect(20,127,44,15);
        add(snsRight);        

        laFront = new Label("");
        laFront.setRect(70,95,44,15);
        add(laFront);
        laLeft = new Label("");
        laLeft.setRect(70,111,44,15);
        add(laLeft);
        laRight = new Label("");
        laRight.setRect(70,127,44,15);
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

    buf[0] = (byte)(0x90+s);    
    sendCommand(buf,0,1);
    serialPort.readBytes(buf,0,1);
    int ret = buf[0];
    if(ret<0) ret=ret+256;
    StringBuffer sb = new StringBuffer();
    sb.append(ret);
    return sb.toString();    
  }

  private void sendDisplayString(String g)
  {
    buf[0] = (byte)(0xA0);
    sendCommand(buf,0,1);    
    char [] b = g.toCharArray();
    buf[0] = (byte)b.length;
    sendCommand(buf,0,1);
    for(int x=0;x<b.length;++x) {
      buf[0] = (byte)b[x];
      sendCommand(buf,0,1);
    }
  }

  private void updateMotors()
  {    
    buf[0] = (byte)(leftMotor*16 + rightMotor);
    sendCommand(buf,0,1);    
  }

  private void pressButton(int bn)
  {
    buf[0] = (byte)(0x80+bn);
    sendCommand(buf,0,1);
  }

  public void onEvent(Event event)
  {
	if (event.type == ControlEvent.PRESSED)
		{

		  if (event.target == closeButton) {
                        sendDisplayString(" Done.");
			exit(0);	
  		  } else if(event.target == adjLeft) {
                    leftMotor = 0;
                    rightMotor = 1;
                    updateMotors();
                    Vm.sleep(200);
                    leftMotor = 1;
                    rightMotor = 1;
                    updateMotors();
                  } else if(event.target == adjRight) {
                    leftMotor = 1;
                    rightMotor = 0;
                    updateMotors();
                    Vm.sleep(200);
                    leftMotor = 1;
                    rightMotor = 1;
                    updateMotors();
                  } else if(event.target == allForward) {
                    leftMotor = 1;
                    rightMotor = 1;
                    updateMotors();
                  } else if(event.target == spinLeft) {
                    rightMotor = 1;
                    leftMotor = 3;
                    updateMotors();
                  } else if(event.target == spinRight) {
                    rightMotor = 3;
                    leftMotor = 1;
                    updateMotors();
                  } else if(event.target == allStop) {
                    leftMotor = 0;
                    rightMotor = 0;
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
                    String s = readSensor(0);
                    laFront.setText(s);
                  } else if(event.target == snsLeft) {
                    String s = readSensor(1);
                    laLeft.setText(s);
                  } else if(event.target == snsRight) {
                    String s = readSensor(2);
                    laRight.setText(s);
                  }

                  else if(event.target == lcd) {
                    String s = lcdEdit.getText(); 
                    double d = Double.parseDouble(s);
                    d=d*4.35;
                    lcdEdit.setText(""+d);
                    //sendDisplayString(s); 
                  }

        }

  }
}