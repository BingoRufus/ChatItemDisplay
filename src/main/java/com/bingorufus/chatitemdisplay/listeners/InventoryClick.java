package com.bingorufus.chatitemdisplay.listeners;

import com.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.bingorufus.chatitemdisplay.util.iteminfo.ItemStackStuff;
import com.bingorufus.chatitemdisplay.util.iteminfo.MapGiver;
import com.bingorufus.chatitemdisplay.util.string.StringFormatter;
import com.bingorufus.chatitemdisplay.util.string.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class InventoryClick implements Listener {
    final List<PlayerInteractEvent> pies = new ArrayList<>();



    @EventHandler
    public void onClick(final InventoryClickEvent e) {
        if (ChatItemDisplayAPI.getDisplayedManager().getChatItemDisplayInventories().contains(e.getInventory())) {
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
                    container(e.getCurrentItem().clone(), p);
                    return;
                }

            }

            switch (e.getCurrentItem().getType()) {
                case FILLED_MAP:
                    MapGiver.giveMap(p, e.getCurrentItem());
                    return;
                case WRITTEN_BOOK:
                case WRITABLE_BOOK:
                    book(e.getCurrentItem(), p);
                    return;
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
        p.sendMessage(StringFormatter.format(ChatItemConfig.MAP.getCachedValue()));
        p.closeInventory();
        p.getInventory().setItemInMainHand(item);
    }

    public void container(ItemStack item, Player p) {

        BlockStateMeta bsm = ((BlockStateMeta) item.getItemMeta());
        if (bsm == null) return;

        Container c = (Container) bsm.getBlockState();
        Inventory container = c.getInventory();
        if (Arrays.stream(container.getContents()).noneMatch(Objects::nonNull))
            return;
        InventoryType type = container.getType();
        Inventory containerInv = Bukkit.createInventory(null, type, type.getDefaultTitle());

        if (item.getItemMeta().hasDisplayName())
            containerInv = Bukkit.createInventory(null, container.getType(), ItemStackStuff.itemName(item));
        containerInv.setContents(container.getContents());

        ChatItemDisplayAPI.getDisplayedManager().addInventory(containerInv);
        p.openInventory(containerInv);

    }


    public void book(ItemStack item, Player p) {
        // The player.openBook() method was added in 1.14.2, this makes sure the version is atleast 1.14.2
        if (VersionComparator.isRecent(ChatItemDisplay.MINECRAFT_VERSION, "1.14.2") == VersionComparator.Status.BEHIND)
            return;

        if (!item.hasItemMeta()) return;
        if (!(item.getItemMeta() instanceof BookMeta)) return;

        BookMeta bm = (BookMeta) item.getItemMeta();

        if (!bm.hasPages()) bm.setPages("");
        bm.setTitle("ChatItemDisplay Book");
        bm.setAuthor("ChatItemDisplay");
        p.closeInventory();

        // A new Item is created in case it is a book and quil.
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

        book.setItemMeta(bm);
        p.openBook(book);


    }
}
