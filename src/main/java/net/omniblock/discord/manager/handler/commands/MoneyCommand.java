package net.omniblock.discord.manager.handler.commands;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.omniblock.discord.manager.DHandlers;
import net.omniblock.discord.manager.handler.DiscordCommandHandler.DiscordCommand;
import net.omniblock.discord.manager.handler.type.RankType;
import net.omniblock.discord.manager.utils.MessageUtils;
import net.omniblock.discord.manager.utils.UserUtils;
import net.omniblock.discord.manager.utils.MessageUtils.DeadMessage;
import net.omniblock.packets.network.Packets;
import net.omniblock.packets.network.socket.helper.SocketHelper;
import net.omniblock.packets.network.structure.data.PacketSocketData;
import net.omniblock.packets.network.structure.data.PacketStructure.DataType;
import net.omniblock.packets.network.structure.packet.RequestActionExecutorPacket;
import net.omniblock.packets.network.structure.packet.ResposeActionExecutorPacket;
import net.omniblock.packets.network.structure.type.PacketSenderType;
import net.omniblock.packets.network.tool.object.PacketResponder;

public class MoneyCommand implements DiscordCommand {

	@Override
	public boolean execute(Message message, String[] command) {
		
		if(command.length == 0)
			return false;
		
		RankType rank = RankType.NONE;
		
		if(message.getGuild().isMember(message.getAuthor()))
			rank = UserUtils.getRank(message.getGuild().getMember(message.getAuthor()));
		
		if(command.length >= 1 && command.length <= 3) {
			
			if(command[0].equalsIgnoreCase("moneyplayer")) {
				
				Message cache = message.getChannel().sendMessage(
						new EmbedBuilder()
							.setColor(Color.RED)
							.setTitle("💢 ¡Ups te ha faltado un Argumento!")
							.setDescription(
									message.getAuthor().getAsMention() + " El comando `moneyplayer` debe tener especificado como parámetros " + 
									"el nombre del jugador, la acción y la cantidad de dinero, las acciones pueden ser (set, add, remove). \n" +
									"**Ejemplo:** `::moneyplayer Notch set 1000`")
							.setFooter("💣 Este mensaje se autodestruirá en 20 segundos!", null)
							.build()).complete();
				
				MessageUtils.deleteMessageAfter(cache, TimeUnit.SECONDS, 20);
				return true;
				
			}
			
			return false;
			
		}
		
		if(!(rank == RankType.EVERYTHING || rank == RankType.ADMIN)) {
			
			Message cache = message.getChannel().sendMessage(
					new EmbedBuilder()
						.setColor(Color.RED)
						.setTitle(":name_badge: ¡Comando bloqueado!")
						.setDescription(
								message.getAuthor().getAsMention() + " Tu no posees permisos suficientes para ejecutar este " + 
								"comando, si necesitas ayuda contacta con un Administrador. \n")
						.setFooter("💣 Este mensaje se autodestruirá en 30 segundos!", null)
						.build()).complete();
			
			MessageUtils.deleteMessageAfter(cache, TimeUnit.SECONDS, 30);
			return true;
			
		}
		
		if(command[0].equalsIgnoreCase("moneyplayer")) {
			
			Message cache = DHandlers.BOT.cmdChannel.sendMessage(
					new EmbedBuilder()
						.setColor(Color.DARK_GRAY)
						.setTitle("⏳ El comando se encuentra en proceso de ejecución...")
						.setDescription(
								"\n \n**Ejecutor: ** " + message.getAuthor().getAsMention() + " \n" +
								"**Comando: ** Configurar el Dinero `moneyplayer` \n" +
								"**Jugador: ** " + command[1] + " \n" +
								"**Acción: ** " + command[2] + " \n" +
								"**Cantidad: ** " + command[3])
						.setFooter("💣 Este mensaje se autodestruirá en 60 segundos!", null)
						.build()).complete();
			
			DeadMessage deadmsg = MessageUtils.deleteMessageAfter(cache, TimeUnit.SECONDS, 60);
			
			Packets.STREAMER.streamPacketAndRespose(
					new RequestActionExecutorPacket()
						.setRequestAction("moneyrequest")
						.setRequesterPort(SocketHelper.getReceiverPort(PacketSenderType.OMNIDISCORD))
						.setArgs(command[1],
								 command[2],
								 command[3])
						.build().setReceiver(PacketSenderType.OMNICORD),
						
					new PacketResponder<ResposeActionExecutorPacket>() {

						@Override
						public void readRespose(PacketSocketData<ResposeActionExecutorPacket> packetsocketdata) {
							
							MessageEmbed embed = new EmbedBuilder()
									.setColor(Color.CYAN)
									.setTitle("💯 Se ha ejecutado un comando correctamente!")
									.setDescription(
											"\n \n**Ejecutor: ** " + message.getAuthor().getAsMention() + " \n" +
											"**Comando: ** Configurar el Dinero `moneyplayer` \n" +
											"**Jugador: ** " + command[1] + " \n" +
											"**Acción: ** " + command[2] + " \n" +
											"**Cantidad: ** " + command[3] + " \n" +
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