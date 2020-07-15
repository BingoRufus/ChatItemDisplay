package me.BingoRufus.ChatDisplay.ListenersAndExecutors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;

import me.BingoRufus.ChatDisplay.Main;
import me.BingoRufus.ChatDisplay.Utils.ItemStackStuff;

public class InventoryClick implements Listener {
	String Version;
	private Main main;

	public InventoryClick(Main m, String ver) {
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
					if (((BlockStateMeta) e.getCurrentItem().getItemMeta()).getBlockState() instanceof ShulkerBox) {
						ShulkerBox shulker = (ShulkerBox) ((BlockStateMeta) e.getCurrentItem().getItemMeta())
								.getBlockState();
						Inventory ShulkerBoxInventory = Bukkit.createInventory(e.getInventory().getHolder(), 27,
								ItemStackStuff.NameFromItem(e.getCurrentItem(), true));
						ShulkerBoxInventory.setContents(shulker.getInventory().getContents());
						main.invs.add(ShulkerBoxInventory);
						e.getWhoClicked().openInventory(ShulkerBoxInventory);

					}
				}

				if (main.UpToDate(Version.split("[.]"), "1.14.2".split("[.]"))) { // The player.openBook() was added in
																					// Spigot for version 1.14.2 this
																					// checks to make sure the version
																					// is past 1.14.2

					if (e.getCurrentItem().getType().equals(Material.WRITTEN_BOOK)) {
						BookMeta bm = (BookMeta) e.getCurrentItem().getItemMeta().clone();

						if (bm.getPages().isEmpty()) {
							bm.setPages("");
						}
						ItemStack book = e.getCurrentItem().clone();
						book.setItemMeta(bm);
						p.closeInventory();
						p.openBook(book);
					}
					if (e.getCurrentItem().getType().equals(Material.WRITABLE_BOOK)) {
						ItemStack item = e.getCurrentItem().clone();
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
		}
	}
}
