package io.github.bingorufus.chatitemdisplay.listeners;

import io.github.bingorufus.chatitemdisplay.DisplayParser;
import io.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class MessageCommandListener implements Listener {
    private final List<String> msgCmds;
    public MessageCommandListener() {

        msgCmds = ChatItemConfig.getConfig().getStringList("message-command");
        msgCmds.replaceAll(cmd -> { // Makes sure the command ends with a space
            return cmd.trim() + " ";
        });

    }

    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent e) {
        if (!e.getMessage().startsWith("/") || msgCmds.stream().noneMatch(e.getMessage()::startsWith))
            return;
        DisplayParser dp = new DisplayParser(e.getMessage());

        if (!dp.containsDisplay())
            return;
        e.setMessage(dp.format(e.getPlayer()));
    }

}
