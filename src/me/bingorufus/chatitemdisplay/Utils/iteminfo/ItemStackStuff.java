package me.bingorufus.chatitemdisplay.Utils.iteminfo;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class ItemStackStuff {
	ChatItemDisplay m;

	public ItemStackStuff(ChatItemDisplay m) {
		this.m = m;
	}
	public String makeStringPretty(String s) {
		switch (s) {
		default:
			String out = null;
			String[] Nameparts = s.toLowerCase().split("_");
			for (String part : Nameparts) {
				part = part.substring(0, 1).toUpperCase() + part.substring(1);
				if (out == null) {
					out = part;
					continue;
				}

				out += " ";
				out += part;
			}

			return out;
		}
	}

	public String ItemName(ItemStack item) {

		String out = "§r";
		if (item.getType().equals(Material.DRAGON_EGG) || item.getType().equals(Material.ENCHANTED_GOLDEN_APPLE))
			out = "§d";

		if (item.getItemMeta().hasEnchants())
			out = ChatColor.AQUA + "";

		if (item.getItemMeta().hasDisplayName()) {
			out += item.getItemMeta().getDisplayName();
			return out;

		}
		if (item.getItemMeta() instanceof PotionMeta) {
			PotionMeta pm = (PotionMeta) item.getItemMeta();
			if (pm.getBasePotionData().getType().equals(PotionType.UNCRAFTABLE)) {
				out += "Uncraftable ";

			}
			switch (item.getType()) {
			case POTION:
				out += "Potion";
				break;
			case SPLASH_POTION:
				out += "Splash Potion";
				break;
			case LINGERING_POTION:
				out += "Lingering Potion";
				break;
			case TIPPED_ARROW:
				if (out.contains("Uncraftable"))
					out += "Tipped ";
				out += "Arrow";
				break;

			default:
				break;
			}
			if (out.contains("Uncraftable")) {
				return out;
			}
			out += " of ";
			out += makeStringPretty(pm.getBasePotionData().getType().name());
			return out;

		}
		if (item.getType().equals(Material.WRITTEN_BOOK)) {

			BookMeta book = (BookMeta) item.getItemMeta();
			if (book.hasTitle()) {
				return book.getTitle();
			}

		}
		return out + makeStringPretty(item.getType().name()) + ChatColor.RESET;

	}

	public TextComponent NameFromItem(ItemStack item) {

		String out = "§r";
		if (item.getType().equals(Material.DRAGON_EGG) || item.getType().equals(Material.ENCHANTED_GOLDEN_APPLE))
			out = "§d";

		if (item.getItemMeta().hasEnchants())
			out = ChatColor.AQUA + "";

		if (item.getItemMeta().hasDisplayName()) {
			out += ChatColor.ITALIC;
			out += item.getItemMeta().getDisplayName();
			return new TextComponent(out);

		}
		if (item.getType().equals(Material.WRITTEN_BOOK)) {

			BookMeta book = (BookMeta) item.getItemMeta();
			if (book.hasTitle()) {
				return new TextComponent(book.getTitle());
			}

		}
		return new TextComponent(new ItemStackTranslator().translateItemStack(item));


	}

	public String potionBuff(PotionEffect p) {
		return "";

	}

	public String romanNumeralify(Short s) {
		Integer level = s.intValue();
		if (s <= 0)
			return s.toString();
		StringBuilder sb = new StringBuilder();

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
