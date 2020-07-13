package me.BingoRufus.ChatDisplay.ListenersAndExecutors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import me.BingoRufus.ChatDisplay.Main;
import net.md_5.bungee.api.ChatColor;

public class ChatItemReloadExecutor implements CommandExecutor {
	private Main main;

	public ChatItemReloadExecutor(Main m) {
		this.main = m;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("chatitemreload")) {
			if (sender.hasPermission("ChatItemDisplay.reload") || sender instanceof ConsoleCommandSender) {
				this.main.reloadConfigVars();
				sender.sendMessage(ChatColor.GREEN + "ChatItemDisplay Reloaded");
				return true;
			}
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
					this.main.getConfig().getString("messages.missing-permission")));
			return true;
		}
		return false;
	}
}
