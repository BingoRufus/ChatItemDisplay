package com.github.bingorufus.chatitemdisplaybungee;

import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

;

public class DisplayReceiver implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PluginMessageEvent e) {

        if (!e.getTag().equalsIgnoreCase("chatitemdisplay:out"))
            return;
        if (!(e.getReceiver() instanceof Server))
            return;

        Server rec = (Server) e.getReceiver();

        new DisplaySender().relayMessage(rec, e.getData());


    }

}
