package command;

import java.util.*;

public class ExecutableList implements Executable 
{
	private List commandList;
	
	public ExecutableList()
	{
		commandList = new LinkedList();
		
	}	
	
	public List getCommandList() {return commandList;}
	
	public void setCommandList(List commandList) 
	{
		this.commandList = commandList;
	}
	
	public void execute() throws Exception
	{
		Iterator i = commandList.iterator();
		while(i.hasNext()) {
			Executable e = (Executable)i.next();
			e.execute();
		}
	}
		
}
     �