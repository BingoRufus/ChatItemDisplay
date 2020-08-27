package me.bingorufus.chatitemdisplay.executors.display;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventoryInfo;
import me.bingorufus.chatitemdisplay.displayables.DisplayItem;
import me.bingorufus.chatitemdisplay.displayables.DisplayItemInfo;
import me.bingorufus.chatitemdisplay.displayables.Displayable;
import me.bingorufus.chatitemdisplay.util.StringFormatter;
import net.md_5.bungee.api.ChatColor;

public class ViewItemExecutor implements CommandExecutor {
	ChatItemDisplay chatItemDisplay;

	public ViewItemExecutor(ChatItemDisplay m) {
		chatItemDisplay = m;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			if (chatItemDisplay.getConfig().getBoolean("disable-gui")) {
				sender.sendMessage(new StringFormatter().format(
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
			if (chatItemDisplay.displayed.containsKey(target.toUpperCase())) {
				Displayable d = chatItemDisplay.displayed.get(target.toUpperCase());
				if (d instanceof DisplayItem) {
					p.openInventory(new DisplayItemInfo(chatItemDisplay,
						(DisplayItem) d).getInventory());
				return true;
			}
			if (d instanceof DisplayInventory) {
				p.openInventory(new DisplayInventoryInfo(chatItemDisplay, (DisplayInventory) d).getInventory());
				return true;
				}
			}
			sender.sendMessage(new StringFormatter()
					.format(
					chatItemDisplay.getConfig().getString("messages.player-not-displaying-anything")));
			return true;


	}
}
