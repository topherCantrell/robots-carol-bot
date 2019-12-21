
package superwabadriver;

import waba.io.*;
import driver.*;

/**
 * This class implements the BASICStampHandler's abstract methods
 * using the SuperWaba SerialPort class.
 */
public class SWBASICStampHandler extends BASICStampHandler
{
	
	private SerialPort port;
	private byte [] c = new byte[1];
	
	/**
	 * This constructs a new SWBASICStampHander.
	 * @param port the SerialPort connected to the stamp
	 */
	public SWBASICStampHandler(SerialPort port)
	{
		this.port = port;
	}
	
	// abstracts from BASICStampHandler
	
	public void sendByte(byte com)
	{
		c[0] = com;
		port.writeBytes(c,0,1);
	}
	
	public byte readByte()
	{
		port.readBytes(c,0,1);
		return c[0];
	}
		
}
