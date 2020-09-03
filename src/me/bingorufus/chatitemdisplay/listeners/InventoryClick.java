package me.bingorufus.chatitemdisplay.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.Furnace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.util.StringFormatter;
import me.bingorufus.chatitemdisplay.util.VersionComparer;
import me.bingorufus.chatitemdisplay.util.VersionComparer.Status;
import me.bingorufus.chatitemdisplay.util.iteminfo.ItemStackStuff;

public class InventoryClick implements Listener {
	String version;
	private ChatItemDisplay m;
	ItemStackStuff ItemStackStuff;
	List<PlayerInteractEvent> pies = new ArrayList<>();

	public InventoryClick(ChatItemDisplay m, String ver) {
		ItemStackStuff = new ItemStackStuff();
		this.m = m;
		version = ver;

	}

	@EventHandler
	public void onClick(final InventoryClickEvent e) {
		if (m.invs.keySet().contains(e.getInventory())) {
			e.setCancelled(true);
			if (e.getClickedInventory() == null)
				return;
			if (e.getCurrentItem() == null)
				return;
			Player p = (Player) e.getWhoClicked();

			if (e.getClickedInventory().equals(p.getInventory()))
				return;

			if (e.getCurrentItem().getItemMeta() instanceof BlockStateMeta) {
				BlockStateMeta bsm = ((BlockStateMeta) e.getCurrentItem().getItemMeta());
				if (bsm.getBlockState() instanceof Container) {
					container(e.getCurrentItem(), p, m.invs.get(e.getInventory()));
					return;
				}

			}

			Status s = new VersionComparer().isRecent(version, "1.14.2");
			if (!s.equals(Status.BEHIND)) { // The player.openBook() was added in //
											// Spigot for version 1.14.2 this
				// checks to make sure the version
				// is past 1.14.2
				book(e.getCurrentItem(), p);
			}
			if (e.getCurrentItem().getType().equals(Material.FILLED_MAP)) {
				map(e.getCurrentItem(), p);
			}
			if (e.getCurrentItem().getType().isRecord()) {
				Block b = p.getLocation().subtract(0, 10, 0).getBlock();
				BlockData bd = b.getBlockData();
				b.setType(Material.JUKEBOX);

				PlayerInteractEvent pie = new PlayerInteractEvent(p, Action.RIGHT_CLICK_BLOCK, e.getCurrentItem(), b,
						BlockFace.UP);
				pies.add(pie);
				Bukkit.getPluginManager().callEvent(pie);

				b.setBlockData(bd, false);
				p.playEffect(p.getLocation(), Effect.RECORD_PLAY, e.getCurrentItem().getType());
				p.closeInventory();
			}

		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void recordInteract(final PlayerInteractEvent e) { // This just checks if it is a Custom Music disc
		if (!pies.contains(e))
			return;
		if (!e.useItemInHand().equals(Result.DENY))
			e.getPlayer().playEffect(e.getPlayer().getLocation(), Effect.RECORD_PLAY, e.getItem().getType());
	}

	public void map(ItemStack item, Player p) {
		m.viewingMap.put(p, p.getInventory().getItemInMainHand());

		p.sendMessage(new StringFormatter().format(m.getConfig().getString("messages.map-notification")));
		p.closeInventory();
		p.getInventory().setItemInMainHand(item);
	}

	public void container(ItemStack item, Player p, UUID owner) {

		BlockStateMeta bsm = ((BlockStateMeta) item.getItemMeta());

		Inventory container = null;

		Player holder = Bukkit.getOfflinePlayer(owner).getPlayer();

		Container c = (Container) bsm.getBlockState();
		if (c instanceof Furnace && !m.hasProtocollib) {
			return;
		}
		container = c.getInventory();

		boolean isEmpty = Arrays.asList(container.getContents()).stream().allMatch(i -> {
			return i == null;
		});
		if (isEmpty)
			return;
		InventoryType type = container.getType();
		Inventory containerInv = Bukkit.createInventory(holder, type, type.getDefaultTitle());

		if (item.getItemMeta().hasDisplayName())
			containerInv = Bukkit.createInventory(holder, container.getType(), ItemStackStuff.itemName(item));
		containerInv.setContents(container.getContents());

		m.invs.put(containerInv, owner);
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
