package com.bingorufus.chatitemdisplay.listeners;

import com.bingorufus.chatitemdisplay.DisplayParser;
import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import com.bingorufus.chatitemdisplay.util.display.DisplayConditionChecker;
import com.bingorufus.chatitemdisplay.util.logger.DebugLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatDisplayListener implements Listener {


    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        DebugLogger.log(p.getName() + " sent a message");


        DisplayParser dp = new DisplayParser(e.getMessage());
        switch (DisplayConditionChecker.doCancelEvent(e.getPlayer(), dp)) {
            case ACCEPT:
                break;
            case CANCEL:
                e.setCancelled(true);
            case IGNORE:
                return;
        }

        e.setMessage(dp.format(p));
        //Send stuff to bungee
        if (ChatItemConfig.BUNGEE.getCachedValue()) {
            dp.getDisplayables().forEach(display -> BungeeCordSender.send(display, false));
        }
    }




}
