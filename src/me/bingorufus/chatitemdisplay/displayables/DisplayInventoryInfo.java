package me.bingorufus.chatitemdisplay.displayables;

import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.util.DisplayableBroadcaster;
import me.bingorufus.chatitemdisplay.util.StringFormatter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class DisplayInventoryInfo implements DisplayInfo {
	private DisplayInventory inv;
	private ChatItemDisplay m;

	public DisplayInventoryInfo(ChatItemDisplay m, DisplayInventory inv) {
		this.inv = inv;
		this.m = m;
		m.invs.put(inv.getInventory(), inv.getUUID());

	}

	@Override
	public void cmdMsg() {
		TranslatableComponent type = null;
		if (getInventory().getType() == InventoryType.ENDER_CHEST)
			type = new TranslatableComponent("container.enderchest");
		else
			type = new TranslatableComponent("container.inventory");
		String format = new StringFormatter()
				.format(m.getConfig().getString("display-messages.inventory-display-format"))
				.replaceAll("%player%",
						m.getConfig().getBoolean("use-nicks-in-display-message")
								? m.getConfig().getBoolean("strip-nick-colors-message")
										? ChatColor.stripColor(inv.getDisplayName())
										: inv.getDisplayName()
						: inv.getPlayer());
		String[] parts = format.split("((?<=%type%)|(?=%type%))");

		TextComponent whole = new TextComponent();
		for (String part : parts) {
			if (part.equalsIgnoreCase("%type%")) {
				whole.addExtra(type);
				continue;
			}
			whole.addExtra(part);
		}
		whole.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/viewitem " + inv.getPlayer()));

		new DisplayableBroadcaster().broadcast(whole);
	}

	@Override
	public Inventory getInventory() {
		return inv.getInventory();
	}

	@Override
	public TextComponent getHover() {
		TranslatableComponent type = null;
		if (getInventory().getType() == InventoryType.ENDER_CHEST)
			type = new TranslatableComponent("container.enderchest");
		else
			type = new TranslatableComponent("container.inventory");
		String format = new StringFormatter()
				.format(m.getConfig().getString("display-messages.inchat-inventory-format"))
				.replaceAll("%player%", m.getConfig().getBoolean("use-nicks-in-display-message")
						? m.getConfig().getBoolean("strip-nick-colors-message")
								? ChatColor.stripColor(inv.getDisplayName())
								: inv.getDisplayName()
						: inv.getPlayer());
		String[] parts = format.split("((?<=%type%)|(?=%type%))");

		TextComponent whole = new TextComponent();
		for (String part : parts) {
			if (part.equalsIgnoreCase("%type%")) {
				whole.addExtra(type);
				continue;
			}
			whole.addExtra(part);
		}
		whole.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/viewitem " + inv.getPlayer()));

		return whole;
	}

}
