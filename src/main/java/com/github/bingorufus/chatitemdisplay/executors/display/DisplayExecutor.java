package com.github.bingorufus.chatitemdisplay.executors.display;

import com.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.github.bingorufus.chatitemdisplay.api.event.DisplayPreProcessEvent;
import com.github.bingorufus.chatitemdisplay.displayables.Displayable;
import com.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.github.bingorufus.chatitemdisplay.util.Cooldown;
import com.github.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class DisplayExecutor implements CommandExecutor {

    public boolean isEventCancelled(Player p, Displayable d) {
        DisplayPreProcessEvent displayEvent = new DisplayPreProcessEvent(p, d, false);
        Bukkit.getPluginManager().callEvent(displayEvent);
        if (displayEvent.isCancelled()) {
            p.sendMessage(displayEvent.getCancellationMessage());
            return true;
        }
        return false;
    }

    protected boolean isOnCooldown(Player p) {
        Cooldown<Player> c = ChatItemDisplay.getInstance().getDisplayCooldown();

        if (c.isOnCooldown(p)) {
            double secondsRemaining = (double) (Math.round((double) c.getTimeRemaining(p) / 100)) / 10;
            p.sendMessage(new StringFormatter().format(ChatItemConfig.COOLDOWN.replace("%seconds%", "" + secondsRemaining)));
            return true;
        }
        return false;
    }

    protected boolean isDisabled(CommandSender sender) {
        if (ChatItemConfig.COMMANDS_DISABLED) {
            sender.sendMessage(new StringFormatter().format(ChatItemConfig.FEATURE_DISABLED));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You can't do that!");
            return true;
        }
        return false;
    }
}
