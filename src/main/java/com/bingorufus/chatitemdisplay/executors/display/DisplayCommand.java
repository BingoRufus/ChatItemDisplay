package com.bingorufus.chatitemdisplay.executors.display;

import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.api.display.DisplayType;
import com.bingorufus.chatitemdisplay.api.display.Displayable;
import com.bingorufus.chatitemdisplay.api.event.DisplayPreProcessEvent;
import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.bingorufus.chatitemdisplay.util.Cooldown;
import com.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import com.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DisplayCommand extends Command {

    protected final DisplayType<?> type;

    public DisplayCommand(DisplayType<?> type) {
        super(type.getCommand().toLowerCase());
        this.type = type;
    }

    @NotNull
    @Override
    public String getDescription() {
        return type.getCommandDescription();
    }


    @NotNull
    @Override
    public List<String> getAliases() {
        if (type.getAliases() == null) return Collections.emptyList();
        return type.getAliases().stream().map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
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
        Cooldown<Player> c = ChatItemDisplayAPI.getDisplayCooldown();

        if (c.isOnCooldown(p)) {
            double secondsRemaining = (double) (Math.round((double) c.getTimeRemaining(p) / 100)) / 10;
            p.sendMessage(StringFormatter.format(ChatItemConfig.COOLDOWN.getCachedValue().replace("%seconds%", "" + secondsRemaining)));
            return true;
        }

        c.ensureRemoved(p);
        return false;
    }

    protected boolean isDisabled(CommandSender sender) {
        if (!type.isCommandEnabled()) {
            sender.sendMessage(StringFormatter.format(ChatItemConfig.FEATURE_DISABLED.getCachedValue()));
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
        return bytes.length >= 1097152;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (isDisabled(sender)) return true;

        Player p = (Player) sender;
        if (!p.hasPermission(type.getCommandPermission())) {
            p.sendMessage(StringFormatter.format(type.getMissingPermissionMessage()));
            return true;
        }
        if (isOnCooldown(p)) return true;
        if (!type.canBeCreated(p)) {
            return true;
        }
        Displayable displayable = type.initDisplayable(p);

        if (displayable.hasBlacklistedItem()) {
            sender.sendMessage(StringFormatter.format(ChatItemConfig.CONTAINS_BLACKLIST.getCachedValue()));
            return true;
        }

        if (isDisplayableTooLong(displayable)) {
            p.sendMessage(StringFormatter.format(type.getTooLargeMessage()));
            return true;
        }

        if (isEventCancelled(p, displayable)) return true;
        ChatItemDisplayAPI.getDisplayedManager().addDisplayable(displayable);
        if (ChatItemConfig.BUNGEE.getCachedValue())
            BungeeCordSender.send(displayable, true);

        if (!p.hasPermission("chatitemdisplay.cooldownbypass"))
            ChatItemDisplayAPI.getDisplayCooldown().addToCooldown(p);
        displayable.broadcastDisplayable();
        return true;
    }
}
