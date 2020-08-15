package me.bingorufus.chatitemdisplay;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.bingorufus.chatitemdisplay.utils.MessageBroadcaster;
import me.bingorufus.chatitemdisplay.utils.iteminfo.ItemStackStuff;
import me.bingorufus.chatitemdisplay.utils.iteminfo.ItemStackTranslator;
import me.bingorufus.chatitemdisplay.utils.iteminfo.ToolTipRetriever;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
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
	public String playerName;
	public String displayName;
	public boolean fromBungee;


	public Display(ChatItemDisplay m, ItemStack item, String playerName, String displayName, Boolean fromBungee) {
		ItemStackStuff = new ItemStackStuff();
		this.m = m;
		this.playerName = playerName;
		this.item = item;
		this.displayName = displayName;
		this.fromBungee = fromBungee;

		String guiname = m.getConfig().getString("messages.gui-format");
		guiname = ChatColor.translateAlternateColorCodes('&', guiname);


		inventory = Bukkit.createInventory(null, 9, guiname.replaceAll("%player%",
				m.getConfig().getBoolean("use-nicks-in-gui") ? displayName : playerName));
		inventory.setItem(4, item);

		Bukkit.getScheduler().runTask(m, () -> {
			if (!m.invs.contains(inventory)) {
				m.invs.add(inventory);
				m.displaying.put(playerName, inventory);
			}
		});


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
		TextComponent Hover = new TextComponent(getName());
		String ver = Bukkit.getVersion().split("MC: ")[1].split("\\.")[1];
		if (Integer.parseInt(ver) >= 16) { // Is 1.16+

			Item nbtitem = new Item(item.getType().getKey().toString(), item.getAmount(),
					ItemTag.ofNbt(new ItemStackTranslator().getNBT(item)));
			Hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, nbtitem));
		} else {
			Hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder(new ToolTipRetriever(m).getLore(item)).create()));

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
		new MessageBroadcaster().broadcast(m, this, true, this.fromBungee, PreMsg, getHover(), EndMsg);
	}



}
