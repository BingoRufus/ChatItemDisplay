package me.BingoRufus.ChatDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;

import me.BingoRufus.ChatDisplay.Utils.ItemStackStuff;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Display {
	Boolean debug;
	Main m;
	String Version;
	String ItemInfo = null;
	String ItemName = null;
	String GUIName;
	String MsgName;
	Inventory ShulkerBoxInventory;
	String Message;
	String[] playermessage;
	Boolean roman;
	public TextComponent Hover;
	TextComponent PreMsg = new TextComponent();
	TextComponent EndMsg = new TextComponent();
	public ItemStack item;
	Player p;
	Inventory inventory;

	public Display(Main m, Player p) {
		this.m = m;
		debug = m.getConfig().getBoolean("debug-mode");
		roman = m.getConfig().getBoolean("enchantments.use-roman-numerals");
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

	public String getLore() {
		String ItemInfo = ItemStackStuff.NameFromItem(item, false);

		if (item.getType().equals(Material.WRITTEN_BOOK)) {
			BookMeta book = (BookMeta) item.getItemMeta();
			if (book.hasAuthor())
				ItemInfo += ChatColor.GRAY + "\nby " + book.getAuthor();
			if (book.hasGeneration()) {
				ItemInfo += ChatColor.GRAY + "\n" + ItemStackStuff.makeStringPretty(book.getGeneration().toString());
			} else {
				ItemInfo += ChatColor.GRAY + "\nOriginal";

			}
		}
		if (!item.getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {

			if (item.getItemMeta().hasEnchants()) {
				Map<Enchantment, Integer> enchants = item.getItemMeta().getEnchants();
				for (Enchantment ench : item.getItemMeta().getEnchants().keySet()) {
					if (ench.getMaxLevel() == 1 && enchants.get(ench) == ench.getMaxLevel()) {
						ItemInfo += "\n" + ChatColor.GRAY
								+ ItemStackStuff.makeStringPretty(ench.getKey().getKey().toString());
						continue;
					}
					if (!roman) {
						ItemInfo += "\n" + ChatColor.GRAY
								+ ItemStackStuff.makeStringPretty(ench.getKey().getKey().toString()) + " "
								+ enchants.get(ench).shortValue();
					} else {
						ItemInfo += "\n" + ChatColor.GRAY
								+ ItemStackStuff.makeStringPretty(ench.getKey().getKey().toString()) + " "
								+ romanNumeralify(enchants.get(ench).shortValue());
					}

				}
			}
			if (debug)
				Bukkit.getLogger().info("Enchants have been created");
		}
		if (item.getItemMeta() instanceof BlockStateMeta) {
			BlockStateMeta im = (BlockStateMeta) item.getItemMeta();
			if (im.getBlockState() instanceof ShulkerBox) {
				if (debug)
					Bukkit.getLogger().info("Item is a Shulker Box");
				ShulkerBox shulker = (ShulkerBox) im.getBlockState();

				List<ItemStack> Contents = new ArrayList<ItemStack>();


				try {
					for (ItemStack i : shulker.getInventory().getContents()) {
						if (!(i == null))
							Contents.add(i);
					}
				} catch (NullPointerException e) {
					if (debug)
						Bukkit.getLogger().info("Shulker Box is empty");
				}

				if (debug)
					Bukkit.getLogger()
							.info("Shulker box contents have been saved, there are " + Contents.size() + " items");
				for (int i = 0; i < Contents.size(); i++) {
					if (debug)
						Bukkit.getLogger().info("In For loop");
					if (i < 5) {
						ItemInfo += "\n" + ChatColor.WHITE + ItemStackStuff.NameFromItem(Contents.get(i), false) + " x"
								+ Contents.get(i).getAmount();

					} else {
						ItemInfo += "\n" + ChatColor.WHITE + "" + ChatColor.ITALIC + "and " + (Contents.size() - 4)
								+ " more...";
						break;

					}
				}

			}
		}
		if (!item.getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ATTRIBUTES)) {
			// LORE
			if (item.getItemMeta().hasLore()) {
				List<String> lore = item.getItemMeta().getLore();
				for (int i = 0; i < lore.size(); i++) {
					ItemInfo += "\n" + ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + lore.get(i);

				}
			}
			if (debug)
				Bukkit.getLogger().info("Lore has been created");
		}
		ItemInfo += "\n" + ChatColor.DARK_GRAY + item.getType().getKey().toString();

		return ItemInfo;

	}

	public String getName() {
		String ItemName = ItemStackStuff.NameFromItem(item, false);
		if (m.getConfig().getBoolean("messages.remove-item-colors"))
			ItemName = ChatColor.stripColor(ItemName);
		if (m.getConfig().getBoolean("show-item-amount") && item.getAmount() > 1)
			ItemName += " x" + item.getAmount();
		return ItemName + ChatColor.RESET;
	}

	public TextComponent getHover() {
		TextComponent Hover = new TextComponent(getName());
		Hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(getLore()).create()));
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
		Bukkit.spigot().broadcast(PreMsg, getHover(), EndMsg);

	}

	public String romanNumeralify(Short s) {
		Integer level = s.intValue();
		if (s <= 0)
			return s.toString();
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.GRAY);
		if (m.getConfig().getBoolean("enchantments.use-minecraft-style-numerals") && level > 10)
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
