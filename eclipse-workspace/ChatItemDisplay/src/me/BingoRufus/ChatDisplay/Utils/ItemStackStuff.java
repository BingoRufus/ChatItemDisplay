package me.BingoRufus.ChatDisplay.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

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

			out = out + " ";
			out = out + part;
		}
		return out;
	}

	public static String NameFromItem(ItemStack item) {
		if (item.getType().equals(Material.WRITTEN_BOOK)) {

			BookMeta book = (BookMeta) item.getItemMeta();
			if (book.hasTitle()) {
				return book.getTitle();
			}

		}
		if (item.getItemMeta().hasDisplayName()) {
			return item.getItemMeta().getDisplayName();
		} else {
			return makeStringPretty(item.getType().name());
		}

	}
}
