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
	Boolean roman;

	public Display(Main m, Boolean debuginfo) {
		main = m;
		debug = debuginfo;
		roman = m.getConfig().getBoolean("enchantments.use-roman-numerals");
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

			if (HeldItem.getItemMeta().hasEnchants()) {
				Map<Enchantment, Integer> enchants = HeldItem.getItemMeta().getEnchants();
				for (Enchantment ench : HeldItem.getItemMeta().getEnchants().keySet()) {
					if (ench.getMaxLevel() == 1 && enchants.get(ench) == ench.getMaxLevel()) {
						ItemInfo = ItemInfo + "\n" + ChatColor.GRAY
								+ ItemStackStuff.makeStringPretty(ench.getKey().getKey().toString());
						continue;
					}
					if (!roman) {
						ItemInfo = ItemInfo + "\n" + ChatColor.GRAY
								+ ItemStackStuff.makeStringPretty(ench.getKey().getKey().toString()) + " "
								+ enchants.get(ench).shortValue();
					} else {
						ItemInfo = ItemInfo + "\n" + ChatColor.GRAY
								+ ItemStackStuff.makeStringPretty(ench.getKey().getKey().toString()) + " "
								+ romanNumeralify(enchants.get(ench).shortValue());
					}

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
						p.chat(playermessage[0].trim());
					}
					if (playermessage.length > 1) {
						p.chat(playermessage[1].trim());
					}
				});
				return;
			}
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.spigot().sendMessage(PreMsg, Hover, EndMsg);
		}

	}

//Need to convert up to 32767 (XXXMMDCCLXVII)
	public String romanNumeralify(Short s) {
		Integer level = s.intValue();
		if (s <= 0)
			return s.toString();
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.GRAY);
		if (main.getConfig().getBoolean("enchantments.use-minecraft-style-numerals") && level > 10)
			return s.toString();
		if (level >= 10000) {
			for (int i = 0; i < level / 10000; i++) {
				sb.append(ChatColor.UNDERLINE + "X" + ChatColor.GRAY);
			}
			level = level - 10000 * (level / 10000);
		}
		if (level >= 9000) {
			sb.append(ChatColor.UNDERLINE + "IX" + ChatColor.GRAY);
			level = level - 9000;
		}
		if (level >= 5000) {
			sb.append(ChatColor.UNDERLINE + "V" + ChatColor.GRAY);
			level = level - 5000;
		}
		if (level <= 4999 && level >= 4000) {
			sb.append(ChatColor.UNDERLINE + "IV" + ChatColor.GRAY);
			level = level - 4000;
		}
		if (level >= 1000) {
			for (int i = 0; i < level / 1000; i++) {
				sb.append("M");
			}
			level = level - 1000 * (level / 1000);
		}
		if (level >= 900) {
			sb.append("CM");
			level = level - 900;
		}
		if (level >= 500) {
			sb.append("D");
			level = level - 500;
		}
		if (level <= 499 && level >= 400) {
			sb.append("CD");
			level = level - 400;
		}
		if (level >= 100) {
			for (int i = 0; i < level / 100; i++) {
				sb.append("C");
			}
			level = level - 100 * (level / 100);
		}
		if (level >= 90) {
			sb.append("XC");
			level = level - 90;
		}
		if (level >= 50) {
			sb.append("L");
			level = level - 50;
		}
		if (level <= 49 && level >= 40) {
			sb.append("XL");
			level = level - 40;
		}
		if (level >= 10) {
			for (int i = 0; i < level / 10; i++) {
				sb.append("X");
			}
			level = level - 10 * (level / 10);
		}
		if (level >= 9) {
			sb.append("IX");
			level = level - 9;
		}
		if (level >= 5) {
			sb.append("V");
			level = level - 5;
		}
		if (level == 4) {
			sb.append("IV");
			level = level - 4;
		}
		if (level >= 1) {
			for (int i = 0; i < level; i++) {
				sb.append("I");
			}
		}

		return sb.toString();

	}

}
