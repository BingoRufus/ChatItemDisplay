package me.bingorufus.chatitemdisplay.executors.display;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import me.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import me.bingorufus.chatitemdisplay.util.iteminfo.PlayerInventoryReplicator;
import me.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DisplayInventoryExecutor implements CommandExecutor {
    final ChatItemDisplay m = ChatItemDisplay.getInstance();


    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You can't do that!");
            return true;
        }

        Player p = (Player) sender;
        if (!p.hasPermission("chatitemdisplay.display.inventory")) {
            p.sendMessage(
                    new StringFormatter().format(m.getConfig().getString("messages.missing-permission-inventory")));
            return true;
        }
        PlayerInventoryReplicator.InventoryData data = new PlayerInventoryReplicator(m).replicateInventory(p);

        DisplayInventory d = new DisplayInventory(data.getInventory(), data.getTitle(), p.getName(),
                p.getDisplayName(), p.getUniqueId(), false);

        m.getDisplayedManager().addDisplayable(p.getName().toUpperCase(), d);
        new BungeeCordSender(m).send(d, true);
        d.getInfo().cmdMsg();

        return true;


    }
}
