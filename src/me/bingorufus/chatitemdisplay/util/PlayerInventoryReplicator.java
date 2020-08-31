package me.bingorufus.chatitemdisplay.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;

public class PlayerInventoryReplicator {
	ChatItemDisplay m;
	public PlayerInventoryReplicator(ChatItemDisplay m) {
		this.m = m;
	}

	public InventoryData replicateInventory(Player p) {
		String invTitle = new StringFormatter()
				.format(m.getConfig().getString("display-messages.displayed-inventory-title").replaceAll("%player%",
						m.getConfig().getBoolean("use-nicks-in-gui") ? m.getConfig().getBoolean("strip-nick-colors-gui")
								? ChatColor.stripColor(p.getDisplayName())
								: p.getDisplayName() : p.getName()));
		Inventory inv = Bukkit.createInventory(Bukkit.getOfflinePlayer(p.getUniqueId()).getPlayer(), 45, invTitle);
		PlayerInventory i = p.getInventory();
		inv.setItem(0, i.getHelmet());
		inv.setItem(1, i.getChestplate());
		inv.setItem(2, i.getLeggings());
		inv.setItem(3, i.getBoots());
		inv.setItem(8, i.getItemInOffHand());
		
		ItemStack[] contents = i.getStorageContents();
		for (int num = 0; num < contents.length; num++) {
			inv.setItem(num < 9 ? num + 36 : num, contents[num]);
		}
		


		return new InventoryData(invTitle, inv);
	}


	public class InventoryData {
		private String title;
		private Inventory inv;

		private InventoryData(String title, Inventory inv) {
			this.title = title;
			this.inv = inv;
		}

		public String getTitle() {
			return title;
		}

		public Inventory getInventory() {
			return inv;
		}

	}
}
