package me.bingorufus.chatitemdisplay.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;

public class DisplayPermissionChecker {
	ChatItemDisplay m;
	Player p;

	public DisplayReason displayItem() {
		Boolean debug = m.getConfig().getBoolean("debug-mode");
		ItemStack i = p.getInventory().getItemInMainHand().clone();
		if (i.getItemMeta() == null) {
			if (debug)
				Bukkit.getLogger().info(p.getName() + "'s item has no meta data");
			return DisplayReason.NULL_ITEM;
		}
		if (!p.hasPermission("chatitemdisplay.display.item")) {
			if (debug)
				Bukkit.getLogger().info(p.getName() + " does not have permission to display items");

			return DisplayReason.NO_PERMISSON;
		}
		if (m.getConfig().getStringList("blacklisted-items").contains(i.getType().getKey().toString())) {
			if (!p.hasPermission("Chatitemdisplay.blacklistbypass")) {
				if (debug)
					Bukkit.getLogger().info(p.getName() + "'s displayed item was blacklisted");
				return DisplayReason.BLACKLISTED;
			}
		}
		if (isOnCooldown()) {
			if (debug)
				Bukkit.getLogger().info(p.getName() + " is on a chat display cooldown");
			return DisplayReason.COOLDOWN;
		}
		return DisplayReason.DISPLAY;
	}

	public boolean hasItemPermission() {
		return false;
	}



	public boolean isOnCooldown() {
		if (m.DisplayCooldowns.containsKey(p.getUniqueId())) {
			Long CooldownRemaining = (m.getConfig().getLong("display-cooldown") * 1000)
					- (System.currentTimeMillis() - m.DisplayCooldowns.get(p.getUniqueId()));

			if (CooldownRemaining > 0) {


				return true;

			}
		}

		return false;

	}

	public DisplayPermissionChecker(ChatItemDisplay chatItemDisplay, Player p) {
		this.m = chatItemDisplay;
		this.p = p;



	}

	public enum DisplayReason {
		BLACKLISTED, COOLDOWN, NULL_ITEM, NO_PERMISSON, DISPLAY
	}

}
