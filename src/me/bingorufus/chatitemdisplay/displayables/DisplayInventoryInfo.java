package me.bingorufus.chatitemdisplay.displayables;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.util.DisplayableBroadcaster;
import me.bingorufus.chatitemdisplay.util.StringFormatter;
import net.md_5.bungee.api.ChatColor;
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
		String key;
		if (getInventory().getType() == InventoryType.ENDER_CHEST)
			key = "container.enderchest";
		else
			key = "container.inventory";
		String format = new StringFormatter()
				.format(m.getConfig().getString("display-messages.inventory-display-format"))
				.replaceAll("%player%",
						m.getConfig().getBoolean("use-nicks-in-display-message")
								? m.getConfig().getBoolean("strip-nick-colors-message")
										? ChatColor.stripColor(inv.getDisplayName())
										: inv.getDisplayName()
						: inv.getPlayer());

		new DisplayableBroadcaster().broadcast(format(format, key));
	}

	@Override
	public Inventory getInventory() {
		return inv.getInventory();
	}

	private TextComponent format(String format, String key) {
		String[] parts = format.split("((?<=%type%)|(?=%type%))");
		TextComponent whole = new TextComponent();

		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			if (part.equalsIgnoreCase("%type%")) {
				TranslatableComponent type = new TranslatableComponent(key);
				if (i > 0) {
					ChatColor color = whole.getExtra().get(i - 1).getColor();
					if (color == ChatColor.WHITE) {
						color = TextComponent.fromLegacyText(
								org.bukkit.ChatColor.getLastColors(whole.getExtra().get(i - 1).toLegacyText()))[0]
										.getColor();
					}
					type.setColor(color);
				}
				whole.addExtra(type);
				continue;
			}

			TextComponent tc = new TextComponent();
			if (i > 0 && parts[i - 1].contains("%type%") && !part.matches("(?s)(.)*(§)(.)*")) // Checks if the previous
				// object was an item
				// and that it doesn't
				// have a §
				tc.setColor(TextComponent.fromLegacyText(
						org.bukkit.ChatColor.getLastColors(whole.getExtra().get(i - 1).toLegacyText()))[0].getColor());
			tc.setText(part);
			whole.addExtra(tc);
		}

		whole.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/viewitem " + inv.getPlayer()));

		return whole;
	}

	@Override
	public TextComponent getHover() {
		String key;
		if (getInventory().getType() == InventoryType.ENDER_CHEST)
			key = "container.enderchest";
		else
			key = "container.inventory";
		String format = new StringFormatter()
				.format(m.getConfig().getString("display-messages.inchat-inventory-format"))
				.replaceAll("%player%", m.getConfig().getBoolean("use-nicks-in-display-message")
						? m.getConfig().getBoolean("strip-nick-colors-message")
								? ChatColor.stripColor(inv.getDisplayName())
								: inv.getDisplayName()
						: inv.getPlayer());
		return format(format, key);
	}

}
