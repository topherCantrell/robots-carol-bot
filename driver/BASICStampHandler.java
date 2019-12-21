package driver;

/**
 * This abstract class contains the primitives to communicate
 * with a BASIC stamp. Derrived classes must supply support
 * for the serial port.
 */
public abstract class BASICStampHandler
{
	
	private byte readIt;

	/**
	 * This method sends a single byte to the BASIC stamp.
	 * @param com the byte to send
	 */	
	public abstract void sendByte(byte com);
	
	/**
	 * This method reads a single byte from the BASIC stamp.
	 * @return the read byte
	 */
	public abstract byte readByte();
	
	/** 
	 * This method handles the handshaking (echo return) to
	 * send a sequence of bytes to the BASIC stamp.
	 * @param buf the buffer containing the bytes
	 * @param start the staring point in the buffer
	 * @param len the number of bytes to send
	 */
	public void sendCommand(byte [] buf, int start, int len)
    {    
    	for(int x=0;x<len;++x) {
    		sendByte(buf[start+x]);       		
	    	do {
	    	  readIt = readByte();
    	  	} while(readIt!=buf[start+x]);      
	    }		
    }
	
}
