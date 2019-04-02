package net.omniblock.discord.manager.console.cmds;

import net.omniblock.discord.manager.console.CommandCatcher.Command;

public class StopCommand implements Command {

	@Override
	public boolean execute(String command, String[] args) {
		
		if(command.equalsIgnoreCase("stop"))
			System.exit(0);
		
		return false;
		
	}

}
