package io.github.bingorufus.chatitemdisplay.util.loaders;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import io.github.bingorufus.chatitemdisplay.listeners.ChatPacketListener;

public class ProtocolLibRegister {
    final ProtocolManager pm;
    ChatPacketListener packetListener;

    public ProtocolLibRegister() {
        pm = ProtocolLibrary.getProtocolManager();
    }

    public void registerPacketListener() {
        if (packetListener != null) {
            pm.removePacketListener(packetListener);
        }


        packetListener = new ChatPacketListener(ListenerPriority.LOWEST, PacketType.Play.Server.CHAT,
                PacketType.Play.Client.AUTO_RECIPE);

        pm.addPacketListener(packetListener);
    }

}