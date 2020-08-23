package me.bingorufus.chatitemdisplay.executors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import net.md_5.bungee.api.ChatColor;

public class ViewItemExecutor implements CommandExecutor {
	ChatItemDisplay chatItemDisplay;

	public ViewItemExecutor(ChatItemDisplay m) {
		chatItemDisplay = m;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("viewitem")) {
			if (chatItemDisplay.getConfig().getBoolean("disable-gui")) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
						chatItemDisplay.getConfig().getString("messages.gui-disabled")));
				return true;

			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can run this command");
				return true;
			}

			if (args.length != 1)
				return false;
			Player p = (Player) sender;
			String target = args[0];
			if (Bukkit.getPlayer(args[0]) != null) {
				target = Bukkit.getPlayer(args[0]).getName();
			}
			if (chatItemDisplay.displaying.containsKey(target.toUpperCase())) {
				p.openInventory(chatItemDisplay.displaying.get(target.toUpperCase()));
				return true;
			}
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
					chatItemDisplay.getConfig().getString("messages.player-not-displaying-anything")));
			return true;

		}
		return false;
	}
}
