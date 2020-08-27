package me.bingorufus.chatitemdisplay.util.loaders;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.listeners.ChatPacketListener;

public class ProtocolLibRegister {
	ChatItemDisplay m;
	ChatPacketListener packetListener;

	ProtocolManager pm;

	public ProtocolLibRegister(ChatItemDisplay m) {
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