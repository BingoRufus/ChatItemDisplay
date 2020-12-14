package me.bingorufus.chatitemdisplay.executors.display;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.Display;
import me.bingorufus.chatitemdisplay.displayables.Displayable;
import me.bingorufus.chatitemdisplay.util.ChatItemConfig;
import me.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ViewItemExecutor implements CommandExecutor {
    final ChatItemDisplay m = ChatItemDisplay.getInstance();


    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (m.getConfig().getBoolean("disable-gui")) {
            sender.sendMessage(new StringFormatter().format(ChatItemConfig.GUI_DISABLED));
            return true;

        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command");
            return true;
        }

        if (args.length < 1)
            return false;

        Player p = (Player) sender;
        String target = args[0];
        Long id = null;
        boolean usePlayer = false;
        boolean invalidPlayer;
        if (Bukkit.getPlayer(args[0]) != null) {
            target = Bukkit.getPlayer(args[0]).getName();
            usePlayer = true;
        }
        invalidPlayer = m.getDisplayedManager().getMostRecent(target.toUpperCase()) == null;

        if (invalidPlayer && usePlayer) {
            sender.sendMessage(
                    new StringFormatter().format(ChatItemConfig.EMPTY_DISPLAY));
            return true;
        }

        if (invalidPlayer) {
            try {
                id = Long.parseLong(args[0]);

            } catch (NumberFormatException e) {
                sender.sendMessage(new StringFormatter()
                        .format(ChatItemConfig.EMPTY_DISPLAY));
                return true;
            }
            if (m.getDisplayedManager().getDisplayed(id) == null) {
                sender.sendMessage(new StringFormatter().format(ChatItemConfig.INVALID_ID));

                return true;
            }

        }


        Display dis;
        if (id != null)
            dis = m.getDisplayedManager().getDisplayed(id);
        else {
            dis = m.getDisplayedManager().getMostRecent(target.toUpperCase());
        }
        if (dis == null) {
            return false;
        }

        Displayable d = dis.getDisplayable();
        p.openInventory(d.getInfo().getInventory());
        return true;


    }


}
