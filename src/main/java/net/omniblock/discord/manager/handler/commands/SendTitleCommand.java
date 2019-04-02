package net.omniblock.discord.manager.handler.commands;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.omniblock.discord.manager.DHandlers;
import net.omniblock.discord.manager.handler.DiscordCommandHandler.DiscordCommand;
import net.omniblock.discord.manager.utils.MessageUtils;
import net.omniblock.packets.network.Packets;
import net.omniblock.packets.network.structure.packet.PlayerSendTitlePacket;
import net.omniblock.packets.network.structure.type.PacketSenderType;

public class SendTitleCommand implements DiscordCommand {

	@Override
	public boolean execute(Message message, String[] command) {
		
		if(command.length == 0)
			return false;
		
		if(command.length >= 1 && command.length <= 5) {
			
			if(command[0].equalsIgnoreCase("sendtitle")) {
				
				Message cache = message.getChannel().sendMessage(
						new EmbedBuilder()
							.setColor(Color.RED)
							.setTitle("💢 ¡Ups te ha faltado un Argumento!")
							.setDescription(
									message.getAuthor().getAsMention() + " El comando `sendtitle` debe tener los parametros --title y " + 
									"--subtitle para definir el titulo y el subtitulo que se le dará al paquete. \n" + 
									"**Ejemplo:** `::sendtitle Notch --title Hola Notch --subtitle ¿Jugamos?`")
							.setFooter("💣 Este mensaje se autodestruirá en 20 segundos!", null)
							.build()).complete();
				
				MessageUtils.deleteMessageAfter(cache, TimeUnit.SECONDS, 20);
				return true;
				
			}
			
			return false;
			
		}
		
		if(command[0].equalsIgnoreCase("sendtitle")) {
			
			StringBuffer title_buff = new StringBuffer();
			StringBuffer subtitle_buff = new StringBuffer();
			
			Boolean title = null;
			
			for(int i = 2; i < command.length; i++) {
				
				if(command[i].equals("--title") && title == null) {
					
					title = true;
					continue;
					
				}
				
				if(title != null) {
					
					if(title) {
						
						if(!command[i].equals("--subtitle")) {
							
							title_buff.append(command[i] + " ");
							continue;
							
						}
						
						title = false;
						continue;
						
					}
					
					subtitle_buff.append(command[i] + " ");
					continue;
					
				}
				
				Message cache = message.getChannel().sendMessage(
						new EmbedBuilder()
							.setColor(Color.RED)
							.setTitle("💢 ¡Se ha encontrado un error en tu sintaxis!")
							.setDescription(
									message.getAuthor().getAsMention() + " El comando `sendtitle` debe tener los parametros --title y " + 
									"--subtitle para definir el titulo y el subtitulo que se le dará al paquete.")
							.setFooter("💣 Este mensaje se autodestruirá en 20 segundos!", null)
							.build()).complete();
				
				MessageUtils.deleteMessageAfter(cache, TimeUnit.SECONDS, 20);
				break;
				
			}
			
			Packets.STREAMER.streamPacket(
					new PlayerSendTitlePacket()
						.setPlayername(command[1])
						.setTitle(title_buff.toString())
						.setSubtitle(subtitle_buff.toString())
						.build().setReceiver(PacketSenderType.OMNICORD)
					);
			
			Message cache = DHandlers.BOT.cmdChannel.sendMessage(
					new EmbedBuilder()
						.setTitle("💯 Se ha ejecutado un comando correctamente!")
						.setDescription(
								"\n \n**Ejecutor: ** " + message.getAuthor().getAsMention() + " \n" +
								"**Comando: ** Envíar un Titulo `sendtitle` \n" +
								"**Jugador: **" + command[1] + " \n" +
								"**Titulo: ** " + title_buff.toString() + " \n" +
								"**Sub-Titulo: ** " + subtitle_buff.toString())
						.setFooter("💣 Este mensaje se autodestruirá en 30 segundos!", null)
						.build()).complete();
			
			MessageUtils.deleteMessageAfter(cache, TimeUnit.SECONDS, 30);
			return true;
			
		}
		
		return false;
	}

}

