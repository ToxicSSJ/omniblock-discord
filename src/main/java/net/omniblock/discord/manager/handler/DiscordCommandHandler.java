package net.omniblock.discord.manager.handler;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.omniblock.discord.manager.handler.commands.AddSkywarsTagCommand;
import net.omniblock.discord.manager.handler.commands.AddWeekPrizeCommand;
import net.omniblock.discord.manager.handler.commands.BanCommand;
import net.omniblock.discord.manager.handler.commands.BetaKeyCommand;
import net.omniblock.discord.manager.handler.commands.BroadcastMessageCommand;
import net.omniblock.discord.manager.handler.commands.BroadcastTitleCommand;
import net.omniblock.discord.manager.handler.commands.CheckOnlineCommand;
import net.omniblock.discord.manager.handler.commands.ForceBoosterCommand;
import net.omniblock.discord.manager.handler.commands.KickCommand;
import net.omniblock.discord.manager.handler.commands.MaintenanceCommand;
import net.omniblock.discord.manager.handler.commands.MoneyCommand;
import net.omniblock.discord.manager.handler.commands.NetworkIDCommand;
import net.omniblock.discord.manager.handler.commands.PardonCommand;
import net.omniblock.discord.manager.handler.commands.RankCommand;
import net.omniblock.discord.manager.handler.commands.RemoveBetaKeyCommand;
import net.omniblock.discord.manager.handler.commands.RemoveSkywarsTagCommand;
import net.omniblock.discord.manager.handler.commands.SendMessageCommand;
import net.omniblock.discord.manager.handler.commands.SendTitleCommand;
import net.omniblock.discord.manager.handler.commands.SetSkywarsModeCommand;
import net.omniblock.discord.manager.utils.MessageUtils;
import net.omniblock.packets.util.Lists;

public class DiscordCommandHandler {

	protected List<DiscordCommand> registeredCommands = Lists.newArrayList();
	
	{
		
		addCommand(new SendMessageCommand());
		addCommand(new SendTitleCommand());
		addCommand(new ForceBoosterCommand());
		addCommand(new BroadcastMessageCommand());
		addCommand(new BroadcastTitleCommand());
		addCommand(new KickCommand());
		addCommand(new RankCommand());
		addCommand(new BanCommand());
		addCommand(new PardonCommand());
		addCommand(new CheckOnlineCommand());
		addCommand(new MoneyCommand());
		addCommand(new SetSkywarsModeCommand());
		addCommand(new AddSkywarsTagCommand());
		addCommand(new RemoveSkywarsTagCommand());
		addCommand(new NetworkIDCommand());
		addCommand(new AddWeekPrizeCommand());
		addCommand(new MaintenanceCommand());
		addCommand(new BetaKeyCommand());
		addCommand(new RemoveBetaKeyCommand());
	
	}
	
	public void executeCommand(Message message, String[] command) {
		
		for(DiscordCommand dc : registeredCommands) {
			
			if(dc.execute(message, command))
				return;
			
		}
		
		Message cache = message.getChannel().sendMessage(
				new EmbedBuilder()
					.setColor(Color.RED)
					.setTitle("💢 ¡Ese comando no existe!")
					.setDescription(
							message.getAuthor().getAsMention() + " El comando que has colocado no está " + 
							"registrado por el sistema, rectifica la sintaxis. \n" +
							"**Uso General:** `::<comando> <parametros>`")
					.setFooter("💣 Este mensaje se autodestruirá en 30 segundos!", null)
					.build()).complete();
		
		MessageUtils.deleteMessageAfter(cache, TimeUnit.SECONDS, 30);
		return;
		
	}
	
	public void addCommand(DiscordCommand command) {
		
		registeredCommands.add(command);
		return;
		
	}
	
	public interface DiscordCommand {
		
		public boolean execute(Message message, String[] command);
		
	}
	
}
