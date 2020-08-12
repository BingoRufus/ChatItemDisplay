package me.BingoRufus.ChatDisplay.Executors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.BingoRufus.ChatDisplay.Display;
import me.BingoRufus.ChatDisplay.Main;
import me.BingoRufus.ChatDisplay.Utils.DisplayPermissionChecker;
import net.md_5.bungee.api.ChatColor;

public class DisplayCommandExecutor implements CommandExecutor {
	Boolean debug;
	Main main;

	public DisplayCommandExecutor(Main m) {
		debug = m.getConfig().getBoolean("debug-mode");
		main = m;
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
						main.getConfig().getString("messages.not-holding-anything")));
				return true;
			}
			if (new DisplayPermissionChecker(main, p).hasPermission()) {
				new Display(main, p).cmdMsg();
				;
			}
			return true;
		}
		return false;
	}

}
