package me.bingorufus.chatitemdisplay.executors.display;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.displayables.DisplayItem;
import me.bingorufus.chatitemdisplay.util.ChatItemConfig;
import me.bingorufus.chatitemdisplay.util.Cooldown;
import me.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import me.bingorufus.chatitemdisplay.util.display.DisplayPermissionChecker;
import me.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DisplayItemExecutor implements CommandExecutor {
    final ChatItemDisplay m = ChatItemDisplay.getInstance();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You can't do that!");
            return true;
        }

        Player p = (Player) sender;
        switch (new DisplayPermissionChecker(m, p).displayItem()) {
            case DISPLAY:
                DisplayItem d = new DisplayItem(p.getInventory().getItemInMainHand(), p.getName(), p.getDisplayName(),
                        p.getUniqueId(),
                        false);
                if (d.serialize().length() >= Short.MAX_VALUE - 20) {
                    p.sendMessage(new StringFormatter().format(ChatItemConfig.TOO_LARGE_ITEM));
                    return true;
                }

                m.getDisplayedManager().addDisplayable(p.getName().toUpperCase(), d);
                if (ChatItemConfig.BUNGEE)
                    new BungeeCordSender().send(d, true);
                d.getInfo().cmdMsg();
                if (!p.hasPermission("ChatItemDisplay.cooldownbypass"))
                    ChatItemDisplay.getInstance().getDisplayCooldown().addToCooldown(p);
                break;
            case BLACKLISTED:
                p.sendMessage(new StringFormatter()
                        .format(m.getConfig().getString("messages.black-listed-item")));
                break;
            case COOLDOWN:
                Cooldown<Player> cooldown = ChatItemDisplay.getInstance().getDisplayCooldown();
                    double secondsRemaining = (double) (Math.round((double) cooldown.getTimeRemaining(p) / 100)) / 10;
                    p.sendMessage(new StringFormatter().format(ChatItemConfig.COOLDOWN.replace("%seconds%", "" + secondsRemaining)));
                break;
            case NO_PERMISSON:
                p.sendMessage(new StringFormatter()
                        .format(ChatItemConfig.MISSING_PERMISSION_ITEM));
                break;
            case NULL_ITEM:
                p.sendMessage(new StringFormatter()
                        .format(ChatItemConfig.EMPTY_HAND));
                break;
        }

        return true;


    }

}
