package me.BingoRufus.ChatDisplay.ListenersAndExecutors;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;

import me.BingoRufus.ChatDisplay.Main;
import me.BingoRufus.ChatDisplay.Utils.ItemStackStuff;

public class InventoryClick implements Listener {
	String Version;
	private Main main;
	ItemStackStuff ItemStackStuff;

	public InventoryClick(Main m, String ver) {
		ItemStackStuff = new ItemStackStuff(m);
		this.main = m;
		Version = ver;

	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {

		if (main.invs.contains(e.getInventory())) {
			e.setCancelled(true);
			if (e.getClickedInventory() == null)
				return;
			if (e.getCurrentItem() == null)
				return;
			Player p = (Player) e.getWhoClicked();

			if (!e.getClickedInventory().equals(p.getInventory())) {

				if (e.getCurrentItem().getItemMeta() instanceof BlockStateMeta) {
					if (((BlockStateMeta) e.getCurrentItem().getItemMeta()).getBlockState() instanceof Container) {
						container(e.getCurrentItem(), p, e.getInventory().getHolder());
						return;
					}

				}


			if (main.UpToDate(Version.split("[.]"), "1.14.2".split("[.]"))) { // The player.openBook() was added in //
																				// Spigot for version 1.14.2 this
				// checks to make sure the version
				// is past 1.14.2
				book(e.getCurrentItem(), p);
			}
			if (e.getCurrentItem().getType().equals(Material.FILLED_MAP)) {
				map(e.getCurrentItem(), p);
			}
		}

		}

	}

	public void map(ItemStack item, Player p) {
		main.viewingMap.put(p, p.getInventory().getItemInMainHand());

		p.sendMessage(
				ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.map-notification")));
		p.closeInventory();
		p.getInventory().setItemInMainHand(item);
	}

	public void container(ItemStack item, Player p, InventoryHolder h) {
		Container c = (Container) ((BlockStateMeta) item.getItemMeta()).getBlockState();
		Inventory containerInv = Bukkit.createInventory(h, 27, ItemStackStuff.ItemName(item));
		containerInv.setContents(c.getInventory().getContents());
		main.invs.add(containerInv);
		p.openInventory(containerInv);

	}


	public void book(ItemStack item, Player p) {

		if (item.getType().equals(Material.WRITTEN_BOOK)) {
			BookMeta bm = (BookMeta) item.getItemMeta().clone();

			if (bm.getPages().isEmpty()) {
				bm.setPages("");
			}
			ItemStack book = item.clone();
			book.setItemMeta(bm);
			p.closeInventory();
			p.openBook(book);
		}
		if (item.getType().equals(Material.WRITABLE_BOOK)) {
			BookMeta BookAndQuill = (BookMeta) item.getItemMeta();
			BookAndQuill.setTitle("Your Mom");
			BookAndQuill.setAuthor("Your Mom");
			ItemStack WrittenBook = new ItemStack(Material.WRITTEN_BOOK);
			if (BookAndQuill.getPages().isEmpty()) {
				BookAndQuill.setPages("");
			}
			WrittenBook.setItemMeta(BookAndQuill);
			p.closeInventory();
			p.openBook(WrittenBook);

		}

	}
}
