package me.bingorufus.chatitemdisplay.listeners;


import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.block.EnderChest;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.utils.iteminfo.ItemStackStuff;

public class InventoryClick implements Listener {
	String Version;
	private ChatItemDisplay m;
	ItemStackStuff ItemStackStuff;

	public InventoryClick(ChatItemDisplay m, String ver) {
		ItemStackStuff = new ItemStackStuff();
		this.m = m;
		Version = ver;

	}



	@EventHandler
	public void onClick(InventoryClickEvent e) {

		if (m.invs.contains(e.getInventory())) {
			e.setCancelled(true);
			if (e.getClickedInventory() == null)
				return;
			if (e.getCurrentItem() == null)
				return;
			Player p = (Player) e.getWhoClicked();

			if (!e.getClickedInventory().equals(p.getInventory())) {

				if (e.getCurrentItem().getItemMeta() instanceof BlockStateMeta) {
					if (((BlockStateMeta) e.getCurrentItem().getItemMeta()).getBlockState() instanceof Container
							|| ((BlockStateMeta) e.getCurrentItem().getItemMeta()).getBlockState()
							instanceof EnderChest) {
						container(e.getCurrentItem(), p, e.getInventory().getHolder());
						return;
					}

				}


				if (m.UpToDate(Version.split("[.]"), "1.14.2".split("[.]"))) { // The player.openBook() was added in //
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
		m.viewingMap.put(p, p.getInventory().getItemInMainHand());

		p.sendMessage(
				ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("messages.map-notification")));
		p.closeInventory();
		p.getInventory().setItemInMainHand(item);
	}

	public void container(ItemStack item, Player p, InventoryHolder h) {
		Inventory container = null;

		if (((BlockStateMeta) item.getItemMeta()).getBlockState() instanceof EnderChest) {
			container = ((Player) h).getEnderChest();
		} else {
		Container c = (Container) ((BlockStateMeta) item.getItemMeta()).getBlockState();
			if (c instanceof Furnace && !m.hasProtocollib) {
			return;
		}
			container = c.getInventory();
		}

		boolean isEmpty = Arrays.asList(container.getContents()).stream().allMatch(i -> {
			return i == null;
		});
		if (isEmpty)
			return;

		Inventory containerInv = Bukkit.createInventory(h, container.getType());
		if (item.getItemMeta().hasDisplayName())
			containerInv = Bukkit.createInventory(h, container.getType(), ItemStackStuff.ItemName(item));
		containerInv.setContents(container.getContents());
		m.invs.add(containerInv);
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
