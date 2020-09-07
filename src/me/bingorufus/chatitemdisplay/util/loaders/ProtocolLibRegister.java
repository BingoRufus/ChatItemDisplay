package me.bingorufus.chatitemdisplay.util.loaders;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.listeners.ChatPacketListener;
import me.bingorufus.chatitemdisplay.listeners.PrivateMessageListener;

public class ProtocolLibRegister {
	ChatItemDisplay m;
	ChatPacketListener packetListener;
	PrivateMessageListener pmListener;

	ProtocolManager pm;

	public ProtocolLibRegister(ChatItemDisplay m) {
		pm = ProtocolLibrary.getProtocolManager();
		this.m = m;
	}

	public void registerPacketListener() {
		if (packetListener != null) {
			pm.removePacketListener(packetListener);
		}
		if (pmListener != null) {
			pm.removePacketListener(pmListener);
		}
		packetListener = new ChatPacketListener(m, ListenerPriority.LOWEST, PacketType.Play.Server.CHAT,
				PacketType.Play.Client.AUTO_RECIPE);
		pmListener = new PrivateMessageListener(m, ListenerPriority.HIGHEST, PacketType.Play.Client.CHAT);

		pm.addPacketListener(packetListener);
		pm.addPacketListener(pmListener);
	}

}