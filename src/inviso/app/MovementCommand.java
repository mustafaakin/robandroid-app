package inviso.app;

import inviso.app.network.Command;

public class MovementCommand {
	Command cmd;
	long time;
	
	public MovementCommand(Command cmd, long time){
		this.cmd = cmd;
		this.time = time;
	}
	

}
