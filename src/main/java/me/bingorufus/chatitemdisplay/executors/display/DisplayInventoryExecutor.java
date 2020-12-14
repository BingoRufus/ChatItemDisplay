package me.bingorufus.chatitemdisplay.executors.display;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import me.bingorufus.chatitemdisplay.util.ChatItemConfig;
import me.bingorufus.chatitemdisplay.util.Cooldown;
import me.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import me.bingorufus.chatitemdisplay.util.display.DisplayPermissionChecker;
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
                    ChatItemConfig.MISSING_PERMISSION_INVENTORY);
            return true;
        }

        if (new DisplayPermissionChecker(m, p).isOnCooldown()) {
            Cooldown<Player> cooldown = ChatItemDisplay.getInstance().getDisplayCooldown();
            double secondsRemaining = (double) (Math.round((double) cooldown.getTimeRemaining(p) / 100)) / 10;
            p.sendMessage(new StringFormatter().format(ChatItemConfig.COOLDOWN.replace("%seconds%", "" + secondsRemaining)));
            return true;
        }
        PlayerInventoryReplicator.InventoryData data = new PlayerInventoryReplicator().replicateInventory(p);

        DisplayInventory d = new DisplayInventory(data.getInventory(), data.getTitle(), p.getName(),
                p.getDisplayName(), p.getUniqueId(), false);
        if (d.serialize().length() >= Short.MAX_VALUE - 20) {
            p.sendMessage(new StringFormatter().format(ChatItemConfig.TOO_LARGE_INVENTORY));
            return true;
        }

        m.getDisplayedManager().addDisplayable(p.getName().toUpperCase(), d);
        if (ChatItemConfig.BUNGEE)
            new BungeeCordSender().send(d, true);
        d.getInfo().cmdMsg();
        if (!p.hasPermission("chatitemdisplay.cooldownbypass"))
            ChatItemDisplay.getInstance().getDisplayCooldown().addToCooldown(p);

        return true;


    }
}
