package net.omniblock.discord.manager.handler.commands;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.omniblock.discord.manager.DHandlers;
import net.omniblock.discord.manager.handler.DiscordCommandHandler.DiscordCommand;
import net.omniblock.discord.manager.utils.MessageUtils;
import net.omniblock.packets.network.Packets;
import net.omniblock.packets.network.structure.packet.BroadcastMessagePacket;
import net.omniblock.packets.network.structure.type.PacketSenderType;

public class BroadcastMessageCommand implements DiscordCommand {

	@Override
	public boolean execute(Message message, String[] command) {
		
		if(command.length == 0)
			return false;
		
		if(command.length == 1) {
			
			if(command[0].equalsIgnoreCase("broadcastmessage")) {
				
				Message cache = message.getChannel().sendMessage(
						new EmbedBuilder()
							.setColor(Color.RED)
							.setTitle("💢 ¡Ups te ha faltado un Argumento!")
							.setDescription(
									message.getAuthor().getAsMention() + " El comando `broadcastmessage` debe tener especificado como parámetro " + 
									"el mensaje que enviarás. \n" + 
									"**Ejemplo:** `::broadcastmessage ¡Hola Omniblock!`")
							.setFooter("💣 Este mensaje se autodestruirá en 20 segundos!", null)
							.build()).complete();
				
				MessageUtils.deleteMessageAfter(cache, TimeUnit.SECONDS, 20);
				return true;
				
			}
			
			return false;
			
		}
		
		if(command[0].equalsIgnoreCase("broadcastmessage")) {
			
			StringBuffer buff = new StringBuffer();
			boolean author = true, discord = true;
			
			for(int i = 1; i < command.length; i++) {
				
				if(command[i].equals("--noauthor")) {
					
					author = false;
					continue;
					
				}
				
				if(command[i].equals("--nodiscord")) {
					
					discord = false;
					continue;
					
				}
				
				buff.append(command[i] + " ");
				continue;
				
			}
			
			String msg = discord ? "&8&lD&8iscord &9&l» &a" + (author ? "@" + message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator() + " &8&l: &7" + buff.toString() 
																   	  :  buff.toString()) 
								 :  author ? "@" + message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator() + " &8&l: &7" + buff.toString() 
								 		   : buff.toString();
			
			Packets.STREAMER.streamPacket(
					new BroadcastMessagePacket()
						.setMessage(msg)
						.build().setReceiver(PacketSenderType.OMNICORD)
					);
			
			Message cache = DHandlers.BOT.cmdChannel.sendMessage(
					new EmbedBuilder()
						.setTitle("💯 Se ha ejecutado un comando correctamente!")
						.setDescription(
								"\n \n**Ejecutor: ** " + message.getAuthor().getAsMention() + " \n" +
								"**Comando: ** Envíar un mensaje Global `broadcastmessage` \n" +
								"**Mensaje: ** " + buff.toString())
						.setFooter("💣 Este mensaje se autodestruirá en 30 segundos!", null)
						.build()).complete();
			
			MessageUtils.deleteMessageAfter(cache, TimeUnit.SECONDS, 30);
			return true;
			
		}
		
		return false;
	}

}
