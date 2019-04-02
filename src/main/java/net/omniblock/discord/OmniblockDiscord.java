package net.omniblock.discord;

import net.omniblock.discord.manager.DHandlers;
import net.omniblock.packets.OmniPackets;
import net.omniblock.packets.network.socket.Sockets;
import net.omniblock.packets.network.socket.helper.SocketHelper;
import net.omniblock.packets.object.external.SystemType;

public class OmniblockDiscord {

	public static void main(String[] args) {
		
		OmniPackets.setupSystem(SystemType.OMNIDISCORD);
		Sockets.SERVER.startServer(SocketHelper.OMNIDISCORD_SOCKET_PORT);
		
		DHandlers.BOT.startBot();
		
	}
	
}
