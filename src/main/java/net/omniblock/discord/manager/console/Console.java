package net.omniblock.discord.manager.console;

import net.omniblock.discord.manager.console.drawer.ConsoleDrawer;
import net.omniblock.discord.manager.console.reader.ConsoleReader;
import net.omniblock.discord.manager.console.writter.ConsoleWritter;

public class Console {

	public static final ConsoleDrawer DRAWER = new ConsoleDrawer();
	public static final ConsoleWritter WRITTER = new ConsoleWritter();
	public static final Thread READER = new Thread(new ConsoleReader());
	
	static {
		
		READER.start();
		
	}
	
}
