package me.bingorufus.chatitemdisplaybungee;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class DisplayReceiver implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PluginMessageEvent e) {

        if (!e.getTag().equalsIgnoreCase("chatitemdisplay:out"))
            return;
        if (!(e.getReceiver() instanceof UserConnection))
            return;

        UserConnection rec = (UserConnection) e.getReceiver();

        new DisplaySender().relayMessage(rec.getServer(), e.getData());


    }

}
