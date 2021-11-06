package com.bingorufus.chatitemdisplay.executors.display;

import com.bingorufus.chatitemdisplay.Display;
import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ViewItemExecutor implements CommandExecutor {


    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (ChatItemConfig.GUI_DISABLED.getCachedValue()) {
            sender.sendMessage(StringFormatter.format(ChatItemConfig.FEATURE_DISABLED.getCachedValue()));
            return true;

        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command");
            return true;
        }

        if (args.length == 0)
            return false;

        Player p = (Player) sender;
        String target = args[0];
        UUID id = null;
        boolean usePlayer = false;
        boolean invalidPlayer;
        if (Bukkit.getPlayer(args[0]) != null) {
            target = Bukkit.getPlayer(args[0]).getName();
            usePlayer = true;
        }
        invalidPlayer = ChatItemDisplayAPI.getDisplayedManager().getMostRecent(target.toUpperCase()) == null;

        if (invalidPlayer && usePlayer) {
            sender.sendMessage(
                    StringFormatter.format(ChatItemConfig.EMPTY_DISPLAY.getCachedValue()));
            return true;
        }

        if (invalidPlayer) {
            try {
                id = UUID.fromString(args[0]);

            } catch (IllegalArgumentException e) {
                sender.sendMessage(StringFormatter
                        .format(ChatItemConfig.EMPTY_DISPLAY.getCachedValue()));
                return true;
            }
            if (ChatItemDisplayAPI.getDisplayedManager().getDisplayed(id) == null) {
                sender.sendMessage(StringFormatter.format(ChatItemConfig.INVALID_ID.getCachedValue()));
                return true;
            }

        }


        Display dis = id == null ? ChatItemDisplayAPI.getDisplayedManager().getMostRecent(target.toUpperCase()) : ChatItemDisplayAPI.getDisplayedManager().getDisplayed(id);

        if (dis == null) {
            return false;
        }
        Inventory inv = dis.getDisplayable().onViewDisplay(p);
        if (inv != null) {
            ChatItemDisplayAPI.getDisplayedManager().addInventory(inv);
            p.openInventory(inv);
        }
        return true;
    }


}
