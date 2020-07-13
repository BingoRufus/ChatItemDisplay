package me.BingoRufus.ChatDisplay.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import net.md_5.bungee.api.ChatColor;

public class ItemStackStuff {
	public static String makeStringPretty(String s) {
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
		out += ChatColor.RESET;
		return out;
	}

	public static String NameFromItem(ItemStack item) {

		String out = "§r";

		if (item.getItemMeta().hasEnchants())
			out = ChatColor.AQUA + "";

		if (item.getItemMeta().hasDisplayName()) {
			out += ChatColor.ITALIC;
			out += item.getItemMeta().getDisplayName();
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

}
