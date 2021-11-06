package com.bingorufus.chatitemdisplaybungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;


public class DisplayRelay implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void receiveDisplay(final PluginMessageEvent e) {

        if (!e.getTag().equalsIgnoreCase("chatitemdisplay:out"))
            return;
        if (e.getReceiver() instanceof ProxiedPlayer) {
            sendDisplay(((ProxiedPlayer) e.getReceiver()).getServer(), e.getData());
        } else if (e.getReceiver() instanceof Server) {
            sendDisplay((Server) e.getReceiver(), e.getData());
        }

    }

    public void sendDisplay(Server from, byte[] data) {
        ChatItemDisplayBungee.getInstance().getProxy().getServers().values().forEach(server -> {
            if (server.equals(from.getInfo()))
                return;
            server.sendData("chatitemdisplay:in", data);
        });

    }
}
