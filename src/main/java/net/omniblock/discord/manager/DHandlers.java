package net.omniblock.discord.manager;

import java.io.IOException;

import net.omniblock.discord.manager.handler.DiscordBotHandler;
import net.omniblock.discord.manager.handler.DiscordCommandHandler;
import net.omniblock.discord.manager.handler.DiscordWebhookHandler;

public class DHandlers {

	public static final DiscordBotHandler BOT = new DiscordBotHandler();
	
	public static final DiscordCommandHandler COMMAND = new DiscordCommandHandler();
	
	public static DiscordWebhookHandler WEBHOOK;
	
	static {
		
		try {
			
			WEBHOOK = new DiscordWebhookHandler();
			
		} catch (IOException e) { e.printStackTrace(); }
		
	}
	
}
