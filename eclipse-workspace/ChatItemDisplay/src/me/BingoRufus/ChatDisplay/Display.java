package me.BingoRufus.ChatDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;

import me.BingoRufus.ChatDisplay.ListenersAndExecutors.ItemDisplayer;
import me.BingoRufus.ChatDisplay.Utils.ItemStackStuff;
import me.BingoRufus.ChatDisplay.Utils.ViewReset;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Display {
	Boolean debug;
	Main main;
	String Version;
	String ItemInfo = null;
	String ItemName = null;
	String GUIName;
	String MsgName;
	Inventory ShulkerBoxInventory;
	String Message;
	String[] playermessage;

	public Display(Main m, Boolean debuginfo) {
		main = m;
		debug = debuginfo;
	}

	public void doStuff(ItemStack HeldItem, Player p, String message) {

		ItemInfo = ItemStackStuff.NameFromItem(HeldItem);
		TextComponent Hover = new TextComponent();
		Hover.setText(ItemInfo);
		ItemName = ItemInfo;
		if (!(message == null))
			playermessage = message.split("%item%");
		if (HeldItem.getType().equals(Material.WRITTEN_BOOK)) {
			BookMeta book = (BookMeta) HeldItem.getItemMeta();
			if (book.hasAuthor())
				ItemInfo = ItemInfo + ChatColor.GRAY + "\nby " + book.getAuthor();
			if (book.hasGeneration()) {
				ItemInfo = ItemInfo + ChatColor.GRAY + "\n"
						+ ItemStackStuff.makeStringPretty(book.getGeneration().toString());
			} else {
				ItemInfo = ItemInfo + ChatColor.GRAY + "\nOriginal";

			}
		}

		if (main.getConfig().getBoolean("use-nicks-in-display-message")) {
			MsgName = p.getDisplayName();
		} else {
			MsgName = p.getName();
		}
		if (main.getConfig().getBoolean("use-nicks-in-gui")) {
			GUIName = p.getDisplayName();
		} else {
			GUIName = p.getName();
		}
		if (debug)
			Bukkit.getLogger().info("Name Formats have been established");
		TextComponent PreMsg = new TextComponent();
		TextComponent EndMsg = new TextComponent();

		Message = ChatColor.translateAlternateColorCodes('&',
				main.getConfig().getString("messages.display-format").replace("%player%", MsgName));

		if (Message.split("%item%")[0] != null) {
			PreMsg.setText(Message.split("%item%")[0]);
		} else {
			PreMsg.setText("");
		}
		if (Message.split("%item%").length == 2) {
			EndMsg.setText(Message.split("%item%")[1]);
		} else {
			EndMsg.setText("");
		}
		if (!HeldItem.getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
			// ENCHANTS
			if (HeldItem.getItemMeta().hasEnchants()) {
				Map<Enchantment, Integer> enchants = HeldItem.getItemMeta().getEnchants();
				for (Enchantment ench : HeldItem.getItemMeta().getEnchants().keySet()) {

					ItemInfo = ItemInfo + "\n" + ChatColor.GRAY
							+ ItemStackStuff.makeStringPretty(ench.getKey().getKey().toString()) + " "
							+ enchants.get(ench).toString();

				}
			}
			if (debug)
				Bukkit.getLogger().info("Enchants have been created");
		}
		if (HeldItem.getItemMeta() instanceof BlockStateMeta) {
			BlockStateMeta im = (BlockStateMeta) HeldItem.getItemMeta();
			if (im.getBlockState() instanceof ShulkerBox) {
				if (debug)
					Bukkit.getLogger().info("Item is a Shulker Box");
				ShulkerBox shulker = (ShulkerBox) im.getBlockState();
				ShulkerBoxInventory = Bukkit.createInventory(p, 27, ItemName);
				ShulkerBoxInventory.setContents(shulker.getInventory().getContents());
				List<ItemStack> Contents = new ArrayList<ItemStack>();

				if (debug)
					Bukkit.getLogger().info("Shulker Box inventory has been created");
				for (ItemStack i : ShulkerBoxInventory.getContents()) {
					if (!(i == null))
						Contents.add(i);
				}

				if (debug)
					Bukkit.getLogger()
							.info("Shulker box contents have been saved, there are " + Contents.size() + " items");
				for (int i = 0; i < Contents.size(); i++) {
					if (debug)
						Bukkit.getLogger().info("In For loop");
					if (i < 5) {
						ItemInfo = ItemInfo + "\n" + ChatColor.WHITE + ItemStackStuff.NameFromItem(Contents.get(i))
								+ " x" + Contents.get(i).getAmount();

					} else {
						ItemInfo = ItemInfo + "\n" + ChatColor.WHITE + "" + ChatColor.ITALIC + "and "
								+ (Contents.size() - 4) + " more...";
						break;

					}
				}

			}
		}
		if (!HeldItem.getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ATTRIBUTES)) {
			// LORE
			if (HeldItem.getItemMeta().hasLore()) {
				List<String> lore = HeldItem.getItemMeta().getLore();
				for (int i = 0; i < lore.size(); i++) {
					ItemInfo = ItemInfo + "\n" + ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + lore.get(i);
				}
			}
			if (debug)
				Bukkit.getLogger().info("Lore has been created");
		}
		// Create Item Display GUI

		Inventory DisplayGUI = Bukkit.createInventory(p, 9,
				ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.gui-format"))
						.replace("%player%", GUIName));
		if (debug)
			Bukkit.getLogger().info("GUI has been created");

		DisplayGUI.setItem(4, HeldItem);

		if (debug)
			Bukkit.getLogger().info("Added item to GUI");
		ViewReset.reset(p, DisplayGUI);
		if (debug)
			Bukkit.getLogger().info("Closed all open inventories of player's GUI");
		if (ShulkerBoxInventory != null) {
			ItemDisplayer.DisplayedShulkerBox.put(p, ShulkerBoxInventory);

		}
		ItemDisplayer.DisplayedItem.put(p.getName(), DisplayGUI);
		ItemInfo = ItemInfo + "\n" + ChatColor.DARK_GRAY + HeldItem.getType().getKey().toString();

		Hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ItemInfo).create()));
		Hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewitem " + p.getName()));
		if (message != null) {
			if (playermessage.length > 0) {
				Bukkit.getScheduler().runTask(main, () -> {
					if (message.indexOf(playermessage[0]) == 0) {
						p.chat(playermessage[0]);
					}
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.spigot().sendMessage(PreMsg, Hover, EndMsg);
					}
					if (message.indexOf(playermessage[0]) > 0) {
						p.chat(playermessage[0]);
					}
					if (playermessage.length > 1) {
						p.chat(playermessage[1]);
					}
				});
				return;
			}
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.spigot().sendMessage(PreMsg, Hover, EndMsg);
		}

	}

}
