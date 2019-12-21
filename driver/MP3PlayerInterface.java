package driver;

public interface MP3PlayerInterface
{
	
	// Need a METADATA for music ... song lengths, names, etc
	
	// Toggle power on and off
	void setPower(boolean power);	
	boolean isPower();	
		
	// Song control
	void playSong(int songNumber);	
	boolean isPlaying();
	void nextSong();
	void stopSong();
	
	// Toggle pause on and off
	void setPause(boolean pause);	
	boolean isPaused();
		
	
}
 �