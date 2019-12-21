package command;

import driver.*;

public class PlaySongCommand implements Executable
{
	
	private MP3PlayerInterface player;
	private int songLength;
	private int songNumber;	
	private boolean doNext;
	
	// Insert a break check here ... if wall sensor is
	// closer than threshold, stop song and move on	
	
	
	public PlaySongCommand(int songNumber, int songLength, 
		boolean doNext, MP3PlayerInterface player)
	{
		this.songNumber = songNumber;
		this.songLength = songLength;
		this.doNext = doNext;
		this.player = player;
	}
	
	public void execute() throws Exception
	{		
		player.playSong(songNumber);		
		if(doNext) ++songNumber;		
		Thread.sleep(songLength);				
	}
	
}
