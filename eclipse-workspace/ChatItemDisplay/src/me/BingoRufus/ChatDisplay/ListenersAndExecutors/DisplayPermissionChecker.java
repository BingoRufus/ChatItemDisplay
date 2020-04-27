package me.BingoRufus.ChatDisplay.ListenersAndExecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.BingoRufus.ChatDisplay.Display;
import me.BingoRufus.ChatDisplay.Main;
import net.md_5.bungee.api.ChatColor;

public class DisplayPermissionChecker {
	Boolean CancelMessage = false;

	public DisplayPermissionChecker(Main main, Player p, Boolean debug, String message) {
		ItemStack HeldItem = p.getInventory().getItemInMainHand().clone();

		if (HeldItem.getItemMeta() == null) {
			if (debug)
				Bukkit.getLogger().info(p.getName() + "'s item has no meta data");
			return;
		}
		if (!p.hasPermission("chatitemdisplay.display")) {
			if (debug)
				Bukkit.getLogger().info(p.getName() + " does not have permission to display items");

			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
					main.getConfig().getString("messages.missing-permission-to-display")));
			return;
		}
		if (main.getConfig().getStringList("blacklisted-items").contains(HeldItem.getType().getKey().toString())) {
			if (!p.hasPermission("Chatitemdisplay.blacklistbypass")) {
				if (debug)
					Bukkit.getLogger().info(p.getName() + "'s displayed item was blacklisted");

				p.sendMessage(ChatColor.translateAlternateColorCodes('&',
						main.getConfig().getString("messages.black-listed-item")));

				return;
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

			}
		}
		CancelMessage = true;
		new Display(main, debug).doStuff(HeldItem, p, message);

		if (!p.hasPermission("chatitemdisplay.cooldownbypass")) {
			ItemDisplayer.DisplayItemCooldowns.put(p.getUniqueId(), System.currentTimeMillis());
		}
	}

	public Boolean sendMessage() {
		return CancelMessage;
	}
}
