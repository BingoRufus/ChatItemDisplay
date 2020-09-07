package me.bingorufus.chatitemdisplay.displayables;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import com.google.gson.JsonObject;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.util.DisplayableBroadcaster;
import me.bingorufus.chatitemdisplay.util.StringFormatter;
import me.bingorufus.chatitemdisplay.util.VersionComparer;
import me.bingorufus.chatitemdisplay.util.VersionComparer.Status;
import me.bingorufus.chatitemdisplay.util.iteminfo.ItemStackReflection;
import me.bingorufus.chatitemdisplay.util.iteminfo.ItemStackStuff;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.chat.ComponentSerializer;

public class DisplayItemInfo implements DisplayInfo {
	DisplayItem display;
	ChatItemDisplay m;
	ItemStackStuff itemStuff;
	ItemStackReflection itemRetriever;
	Inventory inventory;
	public DisplayItemInfo(ChatItemDisplay m, DisplayItem di) {
		itemRetriever = new ItemStackReflection();
		itemStuff = new ItemStackStuff();
		this.m = m;
		this.display = di;
		String guiname = m.getConfig().getString("messages.gui-format");
		guiname = new StringFormatter().format(guiname);

		inventory = Bukkit.createInventory(Bukkit.getOfflinePlayer(display.getUUID()).getPlayer(), 9,
				guiname.replaceAll("%player%",
						m.getConfig().getBoolean("use-nicks-in-gui") ? m.getConfig().getBoolean("strip-nick-colors-gui")
								? ChatColor.stripColor(display.getDisplayName())
								: display.getDisplayName() : display.getPlayer()));
		inventory.setItem(4, display.getItem());

		if (!m.invs.keySet().contains(inventory)) {
			m.invs.put(inventory, display.getUUID());
		}

	}

	public TextComponent getName() {

		TextComponent ItemName = new TextComponent(itemStuff.getName(display.getItem(), "", false));

		if (m.getConfig().getBoolean("show-item-amount") && display.getItem().getAmount() > 1)
			ItemName.addExtra(" x" + display.getItem().getAmount());
		return ItemName;
	}

	@SuppressWarnings("deprecation")
	public TextComponent baseHover() {
		String color = "";

			color = new StringFormatter().format(m.getConfig().getString("messages.item-color"));
		BaseComponent bc = itemStuff.getName(display.getItem(), color,
				m.getConfig().getBoolean("messages.force-item-colors"));


		TextComponent Hover = new TextComponent(bc);



		Status s = new VersionComparer().isRecent(
				Bukkit.getServer().getVersion().substring(Bukkit.getServer().getVersion().indexOf("(MC: ") + 5,
						Bukkit.getServer().getVersion().indexOf(")")),
				"1.16");
	
		if (s.equals(Status.BEHIND)) {
			JsonObject itemJson = new JsonObject();

			itemJson.addProperty("id", display.getItem().getType().getKey().toString());
			itemJson.addProperty("Count", display.getItem().getAmount());
			boolean hasNbt = itemRetriever.hasNbt(display.getItem());
			if (hasNbt)
				itemJson.addProperty("tag", itemRetriever.getNBT(display.getItem())); // Only adds the nbt data if there
																						// is nbt data
			
			String jsonString = itemJson.toString();
			jsonString = jsonString.replaceAll("\\\"id\\\"", "id").replaceAll("\\\"Count\\\"", "Count") // Removes the
																										// quotes around
																										// the property
																										// names and
																										// around the
																										// property tags

					.replaceAll("\\\\", "");
			if(hasNbt) {
				jsonString = jsonString.replaceAll("\\\"tag\\\":\\\"", "tag:").replaceFirst("(?s)\\\"(?!.*?\\\")", ""); // Removes
																														// the
																														// quotes
																														// arround
																														// the
																														// nbt
																														// tag
			}
			

			if (m.getConfig().getBoolean("debug-mode")) {
				Bukkit.getLogger().info(
						"From NMS: " + ComponentSerializer
								.toString(itemRetriever.getOldHover(display.getItem()).getHoverEvent().getValue()));
				Bukkit.getLogger()
						.info("Created:  " + ComponentSerializer.toString(new ComponentBuilder(jsonString).create()));

			}

			Hover.setHoverEvent(new HoverEvent(Action.SHOW_ITEM, new ComponentBuilder(jsonString).create()));

		} else {

			Hover.setHoverEvent(
					new HoverEvent(Action.SHOW_ITEM, new Item(display.getItem().getType().getKey().toString(),
							display.getItem().getAmount(), ItemTag.ofNbt(itemRetriever.getNBT(display.getItem())))));
		}
		Long id = m.getDisplayedManager().getDisplay(display).getId();
		Hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
				"/viewitem " + (id)));

		return Hover;
	}

	@Override
	public TextComponent getHover() {

		
		String format = (display.getItem().getAmount() > 1
				? m.getConfig().getString("display-messages.inchat-item-format-multiple")
				: m.getConfig().getString("display-messages.inchat-item-format"));

		return format(format);
	}


	@Override
	public void cmdMsg() {

		String format = new StringFormatter().format(display.getItem().getAmount() > 1
				? m.getConfig().getString("display-messages.item-display-format-multiple")
				: m.getConfig().getString("display-messages.item-display-format"));

		new DisplayableBroadcaster().broadcast(format(format));
	}

	private TextComponent format(String s) {
		s = s.replaceAll("%player%",
				m.getConfig().getBoolean("use-nicks-in-display-message")
						? m.getConfig().getBoolean("strip-nick-colors-message")
								? ChatColor.stripColor(display.getDisplayName())
								: display.getDisplayName()
						: display.getPlayer());

		s = new StringFormatter().format(s);

		String[] parts = s.split("((?<=%item%)|(?=%item%)|(?<=%amount%)|(?=%amount%))");
		TextComponent whole = new TextComponent();
		TextComponent base = baseHover();
		BaseComponent prev = null;

		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			if (i > 0)
				prev = TextComponent.fromLegacyText(
						org.bukkit.ChatColor.getLastColors(whole.getExtra().get(i - 1).toLegacyText()))[0];

			if (part.contains("%item%")) {
				whole.addExtra(base);
				continue;
			}
			if (part.contains("%amount%")) {
				TextComponent tc = new TextComponent(display.getItem().getAmount() + "");
				if (i > 0)
					tc.copyFormatting(prev);
				
				whole.addExtra(tc);
				continue;
			}
			
			TextComponent tc = new TextComponent(part);
			if (i > 0 && !part.startsWith("§r"))
				tc.copyFormatting(prev); // Checks if the previous
																								// object was an item
											// and that it doesnt
																								// have a §
			whole.addExtra(tc);
		}
		whole.setHoverEvent(base.getHoverEvent());
		whole.setClickEvent(base.getClickEvent());
		return whole;
	}

	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public String loggerMessage() {
		String format = (display.getItem().getAmount() > 1
				? m.getConfig().getString("display-messages.inchat-item-format-multiple")
				: m.getConfig().getString("display-messages.inchat-item-format"));
		format = format.replaceAll("%amount%", display.getItem().getAmount() + "");
		format = format.replaceAll("%item%", new ItemStackStuff().itemName(display.getItem()));

		return ChatColor.stripColor(new StringFormatter().format(format));
	}

	@Override
	public Displayable getDisplayable() {
		return display;
	}



}
