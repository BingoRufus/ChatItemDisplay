package com.github.bingorufus.chatitemdisplay.executors.display;

import com.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.github.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import com.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.github.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import com.github.bingorufus.chatitemdisplay.util.iteminfo.PlayerInventoryReplicator;
import com.github.bingorufus.chatitemdisplay.util.string.StringFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DisplayInventoryExecutor extends DisplayExecutor {
    final ChatItemDisplay m = ChatItemDisplay.getInstance();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (isDisabled(sender)) return true;

        Player p = (Player) sender;
        if (!p.hasPermission("chatitemdisplay.command.display.inventory")) {
            p.sendMessage(new StringFormatter().format(ChatItemConfig.MISSING_PERMISSION_GENERIC));
            return true;
        }
        if (isOnCooldown(p)) return true;

        PlayerInventoryReplicator.InventoryData data = new PlayerInventoryReplicator().replicateInventory(p);

        DisplayInventory d = new DisplayInventory(data.getInventory(), data.getTitle(), p.getName(),
                p.getDisplayName(), p.getUniqueId());
        if (d.serialize().length() >= Short.MAX_VALUE - 20) {
            p.sendMessage(new StringFormatter().format(ChatItemConfig.TOO_LARGE_INVENTORY));
            return true;
        }
        if (isEventCancelled(p, d)) return true;

        m.getDisplayedManager().addDisplayable(p.getName().toUpperCase(), d);
        if (ChatItemConfig.BUNGEE)
            new BungeeCordSender().send(d, true);
        d.getInfo().broadcastCommandMessage();
        if (!p.hasPermission("chatitemdisplay.cooldownbypass"))
            ChatItemDisplay.getInstance().getDisplayCooldown().addToCooldown(p);

        return true;


    }

}
