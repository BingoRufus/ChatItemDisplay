package me.bingorufus.chatitemdisplay.listeners;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.util.iteminfo.ItemStackStuff;
import me.bingorufus.chatitemdisplay.util.string.StringFormatter;
import me.bingorufus.chatitemdisplay.util.string.VersionComparator;
import me.bingorufus.chatitemdisplay.util.string.VersionComparator.Status;
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

import java.util.*;

public class InventoryClick implements Listener {
    final ItemStackStuff ItemStackStuff;
    final List<PlayerInteractEvent> pies = new ArrayList<>();
    private final ChatItemDisplay m;

    public InventoryClick() {
        ItemStackStuff = new ItemStackStuff();
        this.m = ChatItemDisplay.getInstance();

    }


    @EventHandler
    public void onClick(final InventoryClickEvent e) {
        if (m.invs.containsKey(e.getInventory())) {
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
                    container(e.getCurrentItem().clone(), p, m.invs.get(e.getInventory()));
                    return;
                }

            }

            switch (e.getCurrentItem().getType()) {
                case FILLED_MAP:
                    map(e.getCurrentItem(), p);
                    break;
                case WRITTEN_BOOK:
                case WRITABLE_BOOK:
                    book(e.getCurrentItem(), p);
                    break;
                default:
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
        if (e.getItem() == null) return;
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
        if (bsm == null) return;

        Inventory container;
        Player holder = Bukkit.getOfflinePlayer(owner).getPlayer();
        Container c = (Container) bsm.getBlockState();
        if (c instanceof Furnace && !m.hasProtocollib) {
            return;
        }
        container = c.getInventory();
        if (Arrays.stream(container.getContents()).noneMatch(Objects::nonNull))
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
        // The player.openBook() method was added in 1.14.2, this makes sure the version is atleast 1.14.2
        if (new VersionComparator().isRecent(VersionComparator.MINECRAFT_VERSION, "1.14.2") == Status.BEHIND) return;

        if (!item.hasItemMeta()) return;
        if (!(item.getItemMeta() instanceof BookMeta)) return;

        BookMeta bm = (BookMeta) item.getItemMeta();

        if (!bm.hasPages()) bm.setPages("");
        bm.setTitle("ChatItemDisplay is the best plugin");
        bm.setAuthor("ChatItemDisplay");
        p.closeInventory();

        // A new Item is created in case it is a book and quil.
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

        book.setItemMeta(bm);
        p.openBook(book);


    }
}
