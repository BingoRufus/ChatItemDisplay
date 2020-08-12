package me.bingorufus.chatitemdisplay.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.listeners.ChatDisplayListener;
import net.md_5.bungee.api.ChatColor;

public class DisplayPermissionChecker {
	Boolean CancelMessage = true;
	boolean perm = true;

	public boolean CancelMessage() {
		return CancelMessage;
	}

	public boolean hasPermission() {
		return perm;
	}

	public DisplayPermissionChecker(ChatItemDisplay chatItemDisplay, Player p) {
		Boolean debug = chatItemDisplay.getConfig().getBoolean("debug-mode");
		ItemStack HeldItem = p.getInventory().getItemInMainHand().clone();

		if (HeldItem.getItemMeta() == null) {
			if (debug)
				Bukkit.getLogger().info(p.getName() + "'s item has no meta data");
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
					chatItemDisplay.getConfig().getString("messages.not-holding-anything")));
			perm = false;
			CancelMessage = false;
			return;
		}
		if (!p.hasPermission("chatitemdisplay.display")) {
			if (debug)
				Bukkit.getLogger().info(p.getName() + " does not have permission to display items");

			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
					chatItemDisplay.getConfig().getString("messages.missing-permission-to-display")));
			perm = false;
			CancelMessage = true;
			return;
		}
		if (chatItemDisplay.getConfig().getStringList("blacklisted-items").contains(HeldItem.getType().getKey().toString())) {
			if (!p.hasPermission("Chatitemdisplay.blacklistbypass")) {
				if (debug)
					Bukkit.getLogger().info(p.getName() + "'s displayed item was blacklisted");

				p.sendMessage(ChatColor.translateAlternateColorCodes('&',
						chatItemDisplay.getConfig().getString("messages.black-listed-item")));
				perm = false;
				CancelMessage = true;
				return;
			}

		}

		if (debug)
			Bukkit.getLogger().info(p.getName() + "'s item is not blacklisted");

		if (ChatDisplayListener.DisplayItemCooldowns.containsKey(p.getUniqueId())) {
			Long CooldownRemaining = (chatItemDisplay.getConfig().getLong("display-item-cooldown") * 1000)
					- (System.currentTimeMillis() - ChatDisplayListener.DisplayItemCooldowns.get(p.getUniqueId()));

			if (CooldownRemaining > 0) {
				if (debug)
					Bukkit.getLogger().info(p.getName() + " is on a chat display cooldown");

				Double SecondsRemaining = (double) (Math.round(CooldownRemaining.doubleValue() / 100)) / 10;
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', chatItemDisplay.getConfig()
						.getString("messages.cooldown").replaceAll("%seconds%", "" + SecondsRemaining)));
				CancelMessage = false;
				perm = false;
				return;

			}
		}

		if (!p.hasPermission("chatitemdisplay.cooldownbypass")) {
			ChatDisplayListener.DisplayItemCooldowns.put(p.getUniqueId(), System.currentTimeMillis());
		}
	}

}
