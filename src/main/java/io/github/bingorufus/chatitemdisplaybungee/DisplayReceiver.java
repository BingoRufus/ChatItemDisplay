package io.github.bingorufus.chatitemdisplaybungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;


public class DisplayReceiver implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void on(final PluginMessageEvent e) {

        if (!e.getTag().equalsIgnoreCase("chatitemdisplay:out"))
            return;
        if (e.getReceiver() instanceof ProxiedPlayer) {
            new DisplaySender().relayMessage(((ProxiedPlayer) e.getReceiver()).getServer(), e.getData());
        }
        if (e.getReceiver() instanceof Server) {
            new DisplaySender().relayMessage((Server) e.getReceiver(), e.getData());
        }


    }

}
