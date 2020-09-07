package me.bingorufus.chatitemdisplay.displayables;

import org.bukkit.inventory.Inventory;

import net.md_5.bungee.api.chat.TextComponent;

public interface DisplayInfo {
	public Displayable getDisplayable();

	public String loggerMessage();

	public void cmdMsg();

	public Inventory getInventory();

	public TextComponent getHover();
}
