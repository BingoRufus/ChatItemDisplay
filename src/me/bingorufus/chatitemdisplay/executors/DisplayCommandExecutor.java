package me.bingorufus.chatitemdisplay.executors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.Display;
import me.bingorufus.chatitemdisplay.utils.DisplayPermissionChecker;
import net.md_5.bungee.api.ChatColor;

public class DisplayCommandExecutor implements CommandExecutor {
	Boolean debug;
	ChatItemDisplay chatItemDisplay;

	public DisplayCommandExecutor(ChatItemDisplay m) {
		debug = m.getConfig().getBoolean("debug-mode");
		chatItemDisplay = m;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("displayitem")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You can't do that!");
				return true;
			}

			Player p = (Player) sender;
			if (p.getInventory().getItemInMainHand().getItemMeta() == null) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
						chatItemDisplay.getConfig().getString("messages.not-holding-anything")));
				return true;
			}
			if (new DisplayPermissionChecker(chatItemDisplay, p).hasPermission()) {
				new Display(chatItemDisplay, p.getInventory().getItemInMainHand(), p.getName(), p.getDisplayName(),
						false)
						.cmdMsg();
				;
			}
			return true;
		}
		return false;
	}

}
