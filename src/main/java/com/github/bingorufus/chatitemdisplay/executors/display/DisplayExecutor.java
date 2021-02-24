package com.github.bingorufus.chatitemdisplay.executors.display;

import com.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.github.bingorufus.chatitemdisplay.api.display.DisplayType;
import com.github.bingorufus.chatitemdisplay.api.display.Displayable;
import com.github.bingorufus.chatitemdisplay.api.event.DisplayPreProcessEvent;
import com.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.github.bingorufus.chatitemdisplay.util.Cooldown;
import com.github.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import com.github.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public class DisplayExecutor implements CommandExecutor {
    protected final DisplayType type;

    public DisplayExecutor(DisplayType type) {
        this.type = type;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (isDisabled(sender)) return true;

        Player p = (Player) sender;
        if (!p.hasPermission(type.getCommandPermission())) {
            p.sendMessage(new StringFormatter().format(type.getMissingPermissionMessage()));
            return true;
        }
        if (isOnCooldown(p)) return true;
        if (!type.canBeCreated(p)) {
            return true;
        }
        Displayable d = type.initDisplayable(p);

        if (d.hasBlacklistedItem()) {
            sender.sendMessage(new StringFormatter().format(ChatItemConfig.CONTAINS_BLACKLIST));
            return true;
        }

        if (isDisplayableTooLong(d)) {
            p.sendMessage(new StringFormatter().format(type.getTooLargeMessage()));
            return true;
        }

        if (isEventCancelled(p, d)) return true;
        ChatItemDisplay.getInstance().getDisplayedManager().addDisplayable(d);
        if (ChatItemConfig.BUNGEE)
            new BungeeCordSender().send(d, true);

        if (!p.hasPermission("chatitemdisplay.cooldownbypass"))
            ChatItemDisplay.getInstance().getDisplayCooldown().addToCooldown(p);
        d.broadcastDisplayable();
        return true;

    }

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

    public boolean isDisplayableTooLong(Displayable displayable) {
        byte[] bytes = displayable.serialize().toString().getBytes(StandardCharsets.UTF_8);
        if (ChatItemConfig.BUNGEE && bytes.length >= 30000) return true; //
        return bytes.length >= 1097152;
    }
}
