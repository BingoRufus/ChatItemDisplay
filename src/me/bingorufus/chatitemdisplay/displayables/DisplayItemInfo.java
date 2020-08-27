package me.bingorufus.chatitemdisplay.displayables;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.utils.DisplayableBroadcaster;
import me.bingorufus.chatitemdisplay.utils.StringFormatter;
import me.bingorufus.chatitemdisplay.utils.VersionComparer;
import me.bingorufus.chatitemdisplay.utils.VersionComparer.Status;
import me.bingorufus.chatitemdisplay.utils.iteminfo.ItemStackStuff;
import me.bingorufus.chatitemdisplay.utils.iteminfo.ItemStackTranslator;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;

public class DisplayItemInfo implements DisplayInfo {
	DisplayItem display;
	ChatItemDisplay m;
	ItemStackStuff itemStuff;
	ItemStackTranslator itemRetriever;
	Inventory inventory;
	public DisplayItemInfo(ChatItemDisplay m, DisplayItem di) {
		itemRetriever = new ItemStackTranslator();
		itemStuff = new ItemStackStuff();
		this.m = m;
		this.display = di;
		String guiname = m.getConfig().getString("messages.gui-format");
		guiname = new StringFormatter().format(guiname);

		inventory = Bukkit.createInventory(Bukkit.getOfflinePlayer(display.getUUID()).getPlayer(), 9,
				guiname.replaceAll("%player%",
						m.getConfig().getBoolean("use-nicks-in-gui") ? display.getDisplayName() : display.getPlayer()));
		inventory.setItem(4, display.getItem());

		if (!m.invs.keySet().contains(inventory)) {
			m.invs.put(inventory, display.getUUID());
		}

	}

	public TextComponent getName() {
		TextComponent ItemName = itemStuff.NameFromItem(display.getItem());
		if (m.getConfig().getBoolean("messages.remove-item-colors"))
			ItemName.setColor(ChatColor.RESET);
		if (m.getConfig().getBoolean("show-item-amount") && display.getItem().getAmount() > 1)
			ItemName.addExtra(" x" + display.getItem().getAmount());
		return ItemName;
	}

	@SuppressWarnings("deprecation")
	public TextComponent baseHover() {

		TextComponent Hover = new TextComponent(itemStuff.NameFromItem(display.getItem()));
		if (m.getConfig().getBoolean("show-item-amount") && display.getItem().getAmount() > 1)
			Hover.addExtra(" x" + display.getItem().getAmount());
		Status s = new VersionComparer().isRecent(
				Bukkit.getServer().getVersion().substring(Bukkit.getServer().getVersion().indexOf("(MC: ") + 5,
						Bukkit.getServer().getVersion().indexOf(")")),
				"1.16");
		if (s.equals(Status.BEHIND)) {
			JsonObject itemJson = new JsonObject();
			itemJson.addProperty("id", display.getItem().getType().getKey().toString());
			itemJson.addProperty("Count", display.getItem().getAmount());
			itemJson.add("tag", (JsonElement) new JsonParser().parse(itemRetriever.getNBT(display.getItem())));

			String jsonString = itemJson.toString();
			jsonString = jsonString.replaceAll("\\\"id\\\"", "id").replaceAll("\\\"Count\\\"", "Count")
					.replaceAll("\\\"tag\\\"", "tag");
			; // Removes the quotes around the property names and around the nbt tag

			Hover.setHoverEvent(
					new HoverEvent(Action.SHOW_ITEM, new ComponentBuilder(jsonString.replaceAll("\\\\", "")).create()));

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
		
		String format = new StringFormatter().format(m.getConfig().getString("display-messages.inchat-item-format"));
		format = format.replaceAll("%player%",
				m.getConfig().getBoolean("use-nicks-in-display-message") ? display.getDisplayName()
						: display.getPlayer());
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
				m.getConfig().getBoolean("use-nicks-in-display-message") ? display.getDisplayName()
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
