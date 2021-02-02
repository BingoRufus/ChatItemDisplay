package com.github.bingorufus.chatitemdisplay.util.loaders;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.github.bingorufus.chatitemdisplay.listeners.ChatPacketListener;

public class ProtocolLibRegister {
    final ChatItemDisplay m;
    final ProtocolManager pm;
    ChatPacketListener packetListener;

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