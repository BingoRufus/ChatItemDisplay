package me.BingoRufus.ChatDisplay.Executors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.BingoRufus.ChatDisplay.Main;
import net.md_5.bungee.api.ChatColor;

public class ViewItemExecutor implements CommandExecutor {
	Main main;

	public ViewItemExecutor(Main m) {
		main = m;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("viewitem")) {
			if (main.getConfig().getBoolean("disable-gui")) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
						main.getConfig().getString("messages.gui-disabled")));
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
			if (main.displaying.containsKey(target)) {
				p.openInventory(main.displaying.get(target));
				return true;
			}
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
					main.getConfig().getString("messages.player-not-displaying-anything")));
			return true;

		}
		return false;
	}
}
