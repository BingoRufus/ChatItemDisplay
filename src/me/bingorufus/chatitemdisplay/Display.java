package me.bingorufus.chatitemdisplay;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import me.bingorufus.chatitemdisplay.utils.MessageBroadcaster;
import me.bingorufus.chatitemdisplay.utils.VersionComparer;
import me.bingorufus.chatitemdisplay.utils.VersionComparer.Status;
import me.bingorufus.chatitemdisplay.utils.iteminfo.ItemStackStuff;
import me.bingorufus.chatitemdisplay.utils.iteminfo.ItemStackTranslator;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;

public class Display {
	Boolean debug;
	ChatItemDisplay m;
	TextComponent PreMsg = new TextComponent();
	TextComponent EndMsg = new TextComponent();
	public ItemStack item;
	Inventory inventory;
	ItemStackStuff ItemStackStuff;
	private ItemStackTranslator itemRetriever;
	String playerName;
	public String displayName;
	public boolean fromBungee;
	UUID uuid;

	public Display(ChatItemDisplay m, ItemStack item, UUID UUID, String playerName, String displayName,
			Boolean fromBungee) {
		itemRetriever = new ItemStackTranslator();
		ItemStackStuff = new ItemStackStuff();
		this.m = m;
		this.uuid = UUID;
		this.playerName = playerName;
		this.item = item;
		this.displayName = displayName;
		this.fromBungee = fromBungee;

		String guiname = m.getConfig().getString("messages.gui-format");
		guiname = ChatColor.translateAlternateColorCodes('&', guiname);


		inventory = Bukkit.createInventory(Bukkit.getOfflinePlayer(UUID).getPlayer(), 9, guiname
				.replaceAll("%player%",
				m.getConfig().getBoolean("use-nicks-in-gui") ? displayName : playerName));
		inventory.setItem(4, item);

		Bukkit.getScheduler().runTask(m, () -> {
			if (!m.invs.keySet().contains(inventory)) {
				m.invs.put(inventory, uuid);
				m.displaying.put(playerName.toUpperCase(), inventory);
			}
		});


	}

	public String getPlayerName() {
		return this.playerName;
	}

	public UUID getUUID() {
		return this.uuid;
	}


	public TextComponent getName() {
		TextComponent ItemName = ItemStackStuff.NameFromItem(item);
		if (m.getConfig().getBoolean("messages.remove-item-colors"))
			ItemName.setColor(net.md_5.bungee.api.ChatColor.RESET);
		if (m.getConfig().getBoolean("show-item-amount") && item.getAmount() > 1)
			ItemName.addExtra(" x" + item.getAmount());
		return ItemName;
	}

	@SuppressWarnings("deprecation")
	public TextComponent getHover() {


		TextComponent Hover = new TextComponent(ItemStackStuff.NameFromItem(item));
		if (m.getConfig().getBoolean("show-item-amount") && item.getAmount() > 1)
			Hover.addExtra(" x" + item.getAmount());
		Status s = new VersionComparer().isRecent(
				Bukkit.getServer().getVersion().substring(Bukkit.getServer().getVersion().indexOf("(MC: ") + 5,
						Bukkit.getServer().getVersion().indexOf(")")),
				"1.16");
		if(s.equals(Status.BEHIND)) {
			JsonObject itemJson = new  JsonObject();
			itemJson.addProperty("id", item.getType().getKey().toString());
			itemJson.addProperty("Count", item.getAmount());
			itemJson.addProperty("tag", itemRetriever.getNBT(item));
			String jsonString = itemJson.toString();
			jsonString = jsonString.replaceAll("\\\"id\\\"", "id").replaceAll("\\\"Count\\\"", "Count")
					.replaceAll("\\\"tag\\\":\\\"", "tag:").replaceFirst("(?s)" + "\\\"" + "(?!.*?" + "\\\"" + ")", "");
			; // Removes the quotes around the property names and around the nbt tag

			Hover.setHoverEvent(
					new HoverEvent(Action.SHOW_ITEM,
							new ComponentBuilder(jsonString.replaceAll("\\\\", "")).create()));

		}else {

			Hover.setHoverEvent(new HoverEvent(Action.SHOW_ITEM, new Item(item.getType().getKey().toString(),
					item.getAmount(), ItemTag.ofNbt(itemRetriever.getNBT(item)))));
		}

		Hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewitem " + playerName));

		return Hover;
	}


	public void cmdMsg() {

		String format = m.getConfig().getString("messages.display-format");
		format = format.replaceAll("%player%",
				m.getConfig().getBoolean("use-nicks-in-display-message") ? displayName : playerName);
		format = ChatColor.translateAlternateColorCodes('&', format);
		String[] sects = format.split("%item%", 2);
		PreMsg = format.indexOf("%item%") > 0 ? new TextComponent(sects[0]) : new TextComponent("");
		EndMsg = sects.length == 2 ? new TextComponent(sects[1])
				: PreMsg.getText() == null ? new TextComponent(sects[0]) : new TextComponent("");
		new MessageBroadcaster().broadcast(PreMsg, getHover(), EndMsg);
	}



}
