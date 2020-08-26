package me.bingorufus.chatitemdisplay.displayables;

import org.bukkit.inventory.Inventory;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;

public class DisplayInventoryInfo implements DisplayInfo {
	private DisplayInventory inv;
	private ChatItemDisplay m;

	public DisplayInventoryInfo(ChatItemDisplay m, DisplayInventory inv) {
		this.inv = inv;
		this.m = m;

	}

	@Override
	public void cmdMsg() {
		// TODO Auto-generated method stub

	}

	@Override
	public Inventory getInventory() {
		return inv.getInventory();
	}

}
