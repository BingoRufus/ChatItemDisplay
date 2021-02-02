package com.github.bingorufus.chatitemdisplay.executors.display;

import com.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.github.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import com.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.github.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import com.github.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class DisplayEnderChestExecutor extends DisplayExecutor {
    final ChatItemDisplay m = ChatItemDisplay.getInstance();


    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (isDisabled(sender)) return true;


        Player p = (Player) sender;
        if (!p.hasPermission("chatitemdisplay.command.display.enderchest")) {
            p.sendMessage(
                    new StringFormatter().format(ChatItemConfig.MISSING_PERMISSION_GENERIC));
            return true;
        }
        if (isOnCooldown(p)) return true;
        String title = new StringFormatter()
                .format(ChatItemConfig.ENDERCHEST_TITLE.replace("%player%",
                        m.getConfig().getBoolean("use-nicks-in-gui") ? m.getConfig().getBoolean("strip-nick-colors-gui")
                                ? ChatColor.stripColor(p.getDisplayName())
                                : p.getDisplayName() : p.getName()));
        Inventory inv = Bukkit.createInventory(p, InventoryType.ENDER_CHEST, title);
        inv.setContents(p.getEnderChest().getContents());

        DisplayInventory d = new DisplayInventory(inv, title, p.getName(), p.getDisplayName(), p.getUniqueId());

        if (d.serialize().length() >= Short.MAX_VALUE - 20) {
            p.sendMessage(new StringFormatter().format(ChatItemConfig.TOO_LARGE_ENDERCHEST));
            return true;
        }

        if (isEventCancelled(p, d)) return true;

        m.getDisplayedManager().addDisplayable(p.getName().toUpperCase(), d);
        if (ChatItemConfig.BUNGEE)
            new BungeeCordSender().send(d, true);
        d.getInfo().broadcastCommandMessage();
        if (!p.hasPermission("ChatItemDisplay.cooldownbypass"))
            ChatItemDisplay.getInstance().getDisplayCooldown().addToCooldown(p);
        return true;

    }
}
