package me.bingorufus.chatitemdisplay.executors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.util.ConfigReloader;
import me.bingorufus.chatitemdisplay.util.StringFormatter;
import net.md_5.bungee.api.ChatColor;

public class ChatItemReloadExecutor implements CommandExecutor {
	private ChatItemDisplay chatItemDisplay;

	public ChatItemReloadExecutor(ChatItemDisplay m) {
		this.chatItemDisplay = m;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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
