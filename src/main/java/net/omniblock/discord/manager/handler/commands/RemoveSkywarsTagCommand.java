package net.omniblock.discord.manager.handler.commands;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.omniblock.discord.manager.DHandlers;
import net.omniblock.discord.manager.handler.DiscordCommandHandler.DiscordCommand;
import net.omniblock.discord.manager.utils.MessageUtils;
import net.omniblock.discord.manager.utils.MessageUtils.DeadMessage;
import net.omniblock.packets.network.Packets;
import net.omniblock.packets.network.socket.helper.SocketHelper;
import net.omniblock.packets.network.structure.data.PacketSocketData;
import net.omniblock.packets.network.structure.data.PacketStructure.DataType;
import net.omniblock.packets.network.structure.packet.RequestActionExecutorPacket;
import net.omniblock.packets.network.structure.packet.ResposeActionExecutorPacket;
import net.omniblock.packets.network.structure.type.PacketSenderType;
import net.omniblock.packets.network.tool.object.PacketResponder;

public class RemoveSkywarsTagCommand implements DiscordCommand {

	@Override
	public boolean execute(Message message, String[] command) {
		
		if(command.length == 0)
			return false;
		
		if(command.length >= 1 && command.length <= 2) {
			
			if(command[0].equalsIgnoreCase("removeskywarstag")) {
				
				Message cache = message.getChannel().sendMessage(
						new EmbedBuilder()
							.setColor(Color.RED)
							.setTitle("💢 ¡Ups te ha faltado un Argumento!")
							.setDescription(
									message.getAuthor().getAsMention() + " El comando `removeskywarstag` debe tener especificado como parámetros " + 
									"el nombre del jugador y el tag que le quitarás. \n" + 
									"**Ejemplo:** `::removeskywarstag Notch J5`")
							.setFooter("💣 Este mensaje se autodestruirá en 20 segundos!", null)
							.build()).complete();
				
				MessageUtils.deleteMessageAfter(cache, TimeUnit.SECONDS, 20);
				return true;
				
			}
			
			return false;
			
		}
		
		if(command[0].equalsIgnoreCase("removeskywarstag")) {
			
			Message cache = DHandlers.BOT.cmdChannel.sendMessage(
					new EmbedBuilder()
						.setColor(Color.DARK_GRAY)
						.setTitle("⏳ El comando se encuentra en proceso de ejecución...")
						.setDescription(
								"\n \n**Ejecutor: ** " + message.getAuthor().getAsMention() + " \n" +
								"**Comando: ** Remover un tag de Skywars a un Jugador `removeskywarstag` \n" +
								"**Jugador: **" + command[1] + " \n" +
								"**Tag: ** " + command[2])
						.setFooter("💣 Este mensaje se autodestruirá en 60 segundos!", null)
						.build()).complete();
			
			DeadMessage deadmsg = MessageUtils.deleteMessageAfter(cache, TimeUnit.SECONDS, 60);
			
			Packets.STREAMER.streamPacketAndRespose(
					new RequestActionExecutorPacket()
						.setRequestAction("removeskywarstagrequest")
						.setRequesterPort(SocketHelper.getReceiverPort(PacketSenderType.OMNIDISCORD))
						.setArgs(command[1],
								 command[2])
						.build().setReceiver(PacketSenderType.OMNICORD),
						
					new PacketResponder<ResposeActionExecutorPacket>() {

						@Override
						public void readRespose(PacketSocketData<ResposeActionExecutorPacket> packetsocketdata) {
							
							MessageEmbed embed = new EmbedBuilder()
									.setColor(Color.DARK_GRAY)
									.setTitle("💯 Se ha ejecutado un comando correctamente!")
									.setDescription(
											"\n \n**Ejecutor: ** " + message.getAuthor().getAsMention() + " \n" +
											"**Comando: ** Remover un tag de Skywars a un Jugador `removeskywarstag` \n" +
											"**Jugador: **" + command[1] + " \n" +
											"**Tag: ** " + command[2] + " \n" +
											"**Respuesta: ** " + (String) packetsocketdata.getStructure().get(DataType.STRINGS, "response"))
									.setFooter("💣 Este mensaje se autodestruirá en 30 segundos!", null)
									.build(); deadmsg.cancel();
							
							Message completed_cache = null;
									
							if(message.getTextChannel().getMessageById(cache.getId()) != null)
								completed_cache = cache.editMessage(embed).complete();
							else 
								completed_cache = message.getTextChannel().sendMessage(embed).complete();
							
							MessageUtils.deleteMessageAfter(completed_cache, TimeUnit.SECONDS, 30);
							
						}
						
					});
			
			return true;
			
		}
		
		return false;
	}

}
