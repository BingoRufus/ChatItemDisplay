package io.github.bingorufus.chatitemdisplay.executors;

import io.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import io.github.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class ChatItemReloadExecutor implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("chatitemreload")) {
            if (sender.hasPermission("ChatItemDisplay.command.reload") || sender instanceof ConsoleCommandSender) {
                ChatItemConfig.reloadMessages();

                sender.sendMessage(ChatColor.GREEN + "ChatItemDisplay Reloaded");
                return true;
            }


            sender.sendMessage(StringFormatter.format(
                    ChatItemConfig.MISSING_PERMISSION_GENERIC.getCachedValue()));
            return true;
        }
        return false;
    }
}
