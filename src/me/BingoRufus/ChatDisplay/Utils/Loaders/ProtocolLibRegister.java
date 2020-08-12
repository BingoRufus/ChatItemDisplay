package me.BingoRufus.ChatDisplay.Utils.Loaders;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;

import me.BingoRufus.ChatDisplay.Main;
import me.BingoRufus.ChatDisplay.Listeners.ChatPacketListener;

public class ProtocolLibRegister {
	Main m;
	ChatPacketListener packetListener;

	ProtocolManager pm;

	public ProtocolLibRegister(Main m) {
		pm = ProtocolLibrary.getProtocolManager();
		this.m = m;
	}

	public void registerPacketListener() {
		if (packetListener != null) {
			pm.removePacketListener(packetListener);
		}



		packetListener = new ChatPacketListener(m, ListenerPriority.LOWEST, PacketType.Play.Server.CHAT,
				PacketType.Play.Client.AUTO_RECIPE);

		pm.addPacketListener(packetListener);
	}

}