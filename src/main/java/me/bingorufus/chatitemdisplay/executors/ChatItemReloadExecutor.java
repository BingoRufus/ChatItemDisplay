package me.bingorufus.chatitemdisplay.executors;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.util.display.ConfigReloader;
import me.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class ChatItemReloadExecutor implements CommandExecutor {
    private final ChatItemDisplay chatItemDisplay = ChatItemDisplay.getInstance();


    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("chatitemreload")) {
            if (sender.hasPermission("ChatItemDisplay.reload") || sender instanceof ConsoleCommandSender) {
                new ConfigReloader(chatItemDisplay).reload();
                sender.sendMessage(ChatColor.GREEN + "ChatItemDisplay Reloaded");
                return true;
            }


            sender.sendMessage(new StringFormatter().format(
                    this.chatItemDisplay.getConfig().getString("messages.missing-permission")));
            return true;
        }
        return false;
    }
}
