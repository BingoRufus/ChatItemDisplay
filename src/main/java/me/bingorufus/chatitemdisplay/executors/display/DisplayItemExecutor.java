package me.bingorufus.chatitemdisplay.executors.display;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.displayables.DisplayItem;
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
                m.getDisplayedManager().addDisplayable(p.getName().toUpperCase(), d);
                if (ChatItemDisplay.getInstance().isBungee())
                    new BungeeCordSender(m).send(d, true);
                d.getInfo().cmdMsg();
                break;
            case BLACKLISTED:
                p.sendMessage(new StringFormatter()
                        .format(m.getConfig().getString("messages.black-listed-item")));
                break;
            case COOLDOWN:
                long CooldownRemaining = (m.getConfig().getLong("display-cooldown") * 1000)
                        - (System.currentTimeMillis()
                        - m.displayCooldowns.get(p.getUniqueId()));
                double SecondsRemaining = (double) (Math.round((double) CooldownRemaining / 100)) / 10;
                p.sendMessage(new StringFormatter().format(m.getConfig()
                        .getString("messages.cooldown").replaceAll("%seconds%", "" + SecondsRemaining)));
                break;
            case NO_PERMISSON:
                p.sendMessage(new StringFormatter()
                        .format(m.getConfig().getString("messages.missing-permission-to-display")));
                break;
            case NULL_ITEM:
                p.sendMessage(new StringFormatter()
                        .format(m.getConfig().getString("messages.not-holding-anything")));
                break;
        }

        return true;


    }

}
