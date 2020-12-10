package me.bingorufus.chatitemdisplay.executors.display;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import me.bingorufus.chatitemdisplay.util.ChatItemConfig;
import me.bingorufus.chatitemdisplay.util.Cooldown;
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
                    new StringFormatter().format(ChatItemConfig.MISSING_PERMISSION_ENDERCHEST));
            return true;
        }
        if (new DisplayPermissionChecker(m, p).isOnCooldown()) {
            Cooldown<Player> cooldown = ChatItemDisplay.getInstance().getDisplayCooldown();
            double secondsRemaining = (double) (Math.round((double) cooldown.getTimeRemaining(p) / 100)) / 10;
            p.sendMessage(new StringFormatter().format(ChatItemConfig.COOLDOWN.replace("%seconds%", "" + secondsRemaining)));
            return true; // Is on cooldown

        }
        String title = new StringFormatter()
                .format(ChatItemConfig.ENDERCHEST_TITLE.replace("%player%",
                        m.getConfig().getBoolean("use-nicks-in-gui") ? m.getConfig().getBoolean("strip-nick-colors-gui")
                                ? ChatColor.stripColor(p.getDisplayName())
                                : p.getDisplayName() : p.getName()));
        Inventory inv = Bukkit.createInventory(p, InventoryType.ENDER_CHEST, title);
        inv.setContents(p.getEnderChest().getContents());

        DisplayInventory d = new DisplayInventory(inv, title, p.getName(), p.getDisplayName(), p.getUniqueId(), false);

        if (d.serialize().length() >= Short.MAX_VALUE - 20) {
            p.sendMessage(new StringFormatter().format(ChatItemConfig.TOO_LARGE_ENDERCHEST));
            return true;
        }

        m.getDisplayedManager().addDisplayable(p.getName().toUpperCase(), d);
        if (ChatItemConfig.BUNGEE)
            new BungeeCordSender().send(d, true);
        d.getInfo().cmdMsg();
        if (!p.hasPermission("ChatItemDisplay.cooldownbypass"))
            ChatItemDisplay.getInstance().getDisplayCooldown().addToCooldown(p);
        return true;

    }
}
