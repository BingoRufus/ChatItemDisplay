package com.bingorufus.chatitemdisplay.listeners;

import com.bingorufus.chatitemdisplay.DisplayParser;
import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import com.bingorufus.chatitemdisplay.util.display.DisplayConditionChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class MessageCommandListener implements Listener {

    public MessageCommandListener() {
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCmd(PlayerCommandPreprocessEvent e) {
        if (!e.getMessage().startsWith("/") || ChatItemConfig.MESSAGE_COMMANDS.getCachedValue().stream().noneMatch(e.getMessage()::startsWith))
            return;

        DisplayParser dp = new DisplayParser(e.getMessage());
        switch (DisplayConditionChecker.doCancelEvent(e.getPlayer(), dp)) {
            case ACCEPT:
                break;
            case CANCEL:
                e.setCancelled(true);
            case IGNORE:
                return;
        }

        e.setMessage(dp.format(e.getPlayer()));
        if (ChatItemConfig.BUNGEE.getCachedValue()) {
            dp.getDisplayables().forEach(display -> BungeeCordSender.send(display, false));
        }
    }

}
