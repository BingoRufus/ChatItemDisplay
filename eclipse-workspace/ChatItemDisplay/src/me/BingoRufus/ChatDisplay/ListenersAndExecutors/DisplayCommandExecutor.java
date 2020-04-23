package me.BingoRufus.ChatDisplay.ListenersAndExecutors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.BingoRufus.ChatDisplay.Display;
import me.BingoRufus.ChatDisplay.Main;
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
			}
			Player p = (Player) sender;
			ItemStack HeldItem = p.getInventory().getItemInMainHand().clone();

			if (HeldItem.getItemMeta() == null) {
				if (debug)
					Bukkit.getLogger().info(p.getName() + "'s item has no meta data");
				return true;
			}
			if (!p.hasPermission("chatitemdisplay.display")) {
				if (debug)
					Bukkit.getLogger().info(p.getName() + " does not have permission to display items");

				p.sendMessage(ChatColor.translateAlternateColorCodes('&',
						main.getConfig().getString("messages.missing-permission-to-display")));
				return true;
			}
			if (main.getConfig().getStringList("blacklisted-items").contains(HeldItem.getType().getKey().toString())) {
				if (!p.hasPermission("Chatitemdisplay.blacklistbypass")) {
					if (debug)
						Bukkit.getLogger().info(p.getName() + "'s displayed item was blacklisted");

					p.sendMessage(ChatColor.translateAlternateColorCodes('&',
							main.getConfig().getString("messages.black-listed-item")));

					return true;
				}

			}

			if (debug)
				Bukkit.getLogger().info(p.getName() + "'s item is not blacklisted");

			if (ItemDisplayer.DisplayItemCooldowns.containsKey(p.getUniqueId())) {
				Long CooldownRemaining = (main.getConfig().getLong("display-item-cooldown") * 1000)
						- (System.currentTimeMillis() - ItemDisplayer.DisplayItemCooldowns.get(p.getUniqueId()));

				if (CooldownRemaining > 0) {
					if (debug)
						Bukkit.getLogger().info(p.getName() + " is on a chat display cooldown");

					Double SecondsRemaining = (double) (Math.round(CooldownRemaining.doubleValue() / 100)) / 10;
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig()
							.getString("messages.cooldown").replaceAll("%seconds%", "" + SecondsRemaining)));

					return true;
				}
			}
			new Display(main, debug).doStuff(HeldItem, p, null);

			if (!p.hasPermission("chatitemdisplay.cooldownbypass")) {
				ItemDisplayer.DisplayItemCooldowns.put(p.getUniqueId(), System.currentTimeMillis());
			}
		}
		return false;
	}
}
