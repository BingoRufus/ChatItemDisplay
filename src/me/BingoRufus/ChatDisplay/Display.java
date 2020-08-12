package me.BingoRufus.ChatDisplay;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.BingoRufus.ChatDisplay.Utils.MessageBroadcaster;
import me.BingoRufus.ChatDisplay.Utils.ItemInfo.ItemStackStuff;
import me.BingoRufus.ChatDisplay.Utils.ItemInfo.ItemStackTranslator;
import me.BingoRufus.ChatDisplay.Utils.ItemInfo.ToolTipRetriever;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;

public class Display {
	Boolean debug;
	Main m;
	TextComponent PreMsg = new TextComponent();
	TextComponent EndMsg = new TextComponent();
	public ItemStack item;
	Player p;
	Inventory inventory;
	ItemStackStuff ItemStackStuff;


	public Display(Main m, Player p) {
		ItemStackStuff = new ItemStackStuff(m);
		this.m = m;
		this.item = p.getInventory().getItemInMainHand();
		this.p = p;
		String guiname = m.getConfig().getString("messages.gui-format");
		guiname = ChatColor.translateAlternateColorCodes('&', guiname);
		;

		inventory = Bukkit.createInventory(p, 9, guiname.replaceAll("%player%",
				m.getConfig().getBoolean("use-nicks-in-gui") ? p.getDisplayName() : p.getName()));
		inventory.setItem(4, item);

		Bukkit.getScheduler().runTask(m, () -> {
			if (!m.invs.contains(inventory)) {
				m.invs.add(inventory);
				m.displaying.put(p.getName(), inventory);
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
		Hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewitem " + p.getName()));

		return Hover;
	}

	public void cmdMsg() {

		String format = m.getConfig().getString("messages.display-format");
		format = format.replaceAll("%player%",
				m.getConfig().getBoolean("use-nicks-in-display-message") ? p.getDisplayName() : p.getName());
		format = ChatColor.translateAlternateColorCodes('&', format);
		String[] sects = format.split("%item%");
		PreMsg = format.indexOf("%item%") > 0 ? new TextComponent(sects[0]) : new TextComponent("");
		EndMsg = sects.length == 2 ? new TextComponent(sects[1])
				: PreMsg.getText() == null ? new TextComponent(sects[0]) : new TextComponent("");
		new MessageBroadcaster().broadcast(PreMsg, getHover(), EndMsg);
	}



}
