package com.bingorufus.chatitemdisplay.util.bungee;


import com.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.bingorufus.chatitemdisplay.api.display.Displayable;
import com.bingorufus.chatitemdisplay.util.logger.DebugLogger;
import org.bukkit.Bukkit;

public class BungeeCordSender {


    private BungeeCordSender() {

    }

    public static void send(Displayable displayable, boolean isCmd) {
        DisplayPacket[] packets = DisplayPacket.createPackets(displayable, isCmd);
        for (DisplayPacket packet : packets) {
            DebugLogger.log("Sent a packet to bungee proxy");
            Bukkit.getServer().sendPluginMessage(ChatItemDisplay.getInstance(), "chatitemdisplay:out", packet.getData());
        }
    }


}
