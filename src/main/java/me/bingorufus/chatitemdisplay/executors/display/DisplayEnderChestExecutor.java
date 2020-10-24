package me.bingorufus.chatitemdisplay.executors.display;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import me.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import me.bingorufus.chatitemdisplay.util.display.DisplayPermissionChecker;
import me.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class DisplayEnderChestExecutor implements CommandExecutor {
    final ChatItemDisplay m = ChatItemDisplay.getInstance();


    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You can't do that!");
            return true;
        }


        Player p = (Player) sender;
        if (!p.hasPermission("chatitemdisplay.display.enderchest")) {
            p.sendMessage(
                    new StringFormatter().format(m.getConfig().getString("messages.missing-permission-enderchest")));
            return true;
        }
        if (new DisplayPermissionChecker(m, p).isOnCooldown()) {
            long CooldownRemaining = (m.getConfig().getLong("display-cooldown") * 1000)
                    - (System.currentTimeMillis()
                    - m.displayCooldowns.get(p.getUniqueId()));
            double SecondsRemaining = (double) (Math.round((double) CooldownRemaining / 100)) / 10;
            p.sendMessage(new StringFormatter().format(m.getConfig()
                    .getString("messages.cooldown").replace("%seconds%", "" + SecondsRemaining)));
            return true;
        }
        String title = new StringFormatter()
                .format(m.getConfig().getString("display-messages.displayed-enderchest-title").replace("%player%",
                        m.getConfig().getBoolean("use-nicks-in-gui") ? m.getConfig().getBoolean("strip-nick-colors-gui")
                                ? ChatColor.stripColor(p.getDisplayName())
                                : p.getDisplayName() : p.getName()));
        Inventory inv = Bukkit.createInventory(p, InventoryType.ENDER_CHEST, title);
        inv.setContents(p.getEnderChest().getContents());

        DisplayInventory d = new DisplayInventory(inv, title, p.getName(), p.getDisplayName(), p.getUniqueId(), false);

        m.getDisplayedManager().addDisplayable(p.getName().toUpperCase(), d);
        if (ChatItemDisplay.getInstance().isBungee())
            new BungeeCordSender(m).send(d, true);
        d.getInfo().cmdMsg();
        if (!p.hasPermission("ChatItemDisplay.cooldownbypass"))
            m.displayCooldowns.put(p.getUniqueId(), System.currentTimeMillis());
        return true;

    }
}
