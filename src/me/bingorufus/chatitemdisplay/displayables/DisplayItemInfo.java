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
		TextComponent ItemName = new TextComponent(itemStuff.NameFromItem(display.getItem()));

		if (m.getConfig().getBoolean("show-item-amount") && display.getItem().getAmount() > 1)
			ItemName.addExtra(" x" + display.getItem().getAmount());
		return ItemName;
	}

	@SuppressWarnings("deprecation")
	public TextComponent baseHover() {
		String color = "";
		BaseComponent bc;
		if (m.getConfig().getBoolean("messages.remove-item-colors")) {
			color = new StringFormatter().format(m.getConfig().getString("messages.item-color"));
			bc = itemStuff.NameFromItem(display.getItem(), color);
		} else {
			bc = itemStuff.NameFromItem(display.getItem());
		}
		TextComponent Hover = new TextComponent(bc);

		if (m.getConfig().getBoolean("show-item-amount") && display.getItem().getAmount() > 1)
			Hover.addExtra(bc.getColor() + " x" + display.getItem().getAmount());



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

		Hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewitem " + display.getPlayer()));

		return Hover;
	}

	@Override
	public TextComponent getHover() {

		TextComponent base= baseHover();
		

		String format = m.getConfig().getString("display-messages.inchat-item-format").replaceAll("%player%",
				m.getConfig().getBoolean("use-nicks-in-display-message")
						? m.getConfig().getBoolean("strip-nick-colors-message")
								? ChatColor.stripColor(display.getDisplayName())
								: display.getDisplayName()
						: display.getPlayer());
		format = new StringFormatter().format(format);

		String[] parts = format.split("((?<=%item%)|(?=%item%))");
		TextComponent whole = new TextComponent();
		for (String part : parts) {
			if (part.equalsIgnoreCase("%item%")) {
				whole.addExtra(base);
				continue;
			}
			whole.addExtra(part);
		}
		whole.setHoverEvent(base.getHoverEvent());
		whole.setClickEvent(base.getClickEvent());
		return whole;
	}


	@Override
	public void cmdMsg() {

		String format = new StringFormatter().format(m.getConfig().getString("display-messages.item-display-format"));
		format = format.replaceAll("%player%",
				m.getConfig().getBoolean("use-nicks-in-display-message")
						? m.getConfig().getBoolean("strip-nick-colors-message")
								? ChatColor.stripColor(display.getDisplayName())
								: display.getDisplayName()
						: display.getPlayer());
		format = new StringFormatter().format(format);
		String[] sects = format.split("%item%", 2);
		TextComponent PreMsg = format.indexOf("%item%") > 0 ? new TextComponent(sects[0]) : new TextComponent("");
		TextComponent EndMsg = sects.length == 2 ? new TextComponent(sects[1])
				: PreMsg.getText() == null ? new TextComponent(sects[0]) : new TextComponent("");
		new DisplayableBroadcaster().broadcast(PreMsg, baseHover(), EndMsg);
	}

	public Inventory getInventory() {
		return inventory;
	}




}
