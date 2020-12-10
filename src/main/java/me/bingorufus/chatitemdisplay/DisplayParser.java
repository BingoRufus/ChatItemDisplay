package me.bingorufus.chatitemdisplay;

import me.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import me.bingorufus.chatitemdisplay.displayables.DisplayItem;
import me.bingorufus.chatitemdisplay.displayables.Displayable;
import me.bingorufus.chatitemdisplay.util.ChatItemConfig;
import me.bingorufus.chatitemdisplay.util.iteminfo.PlayerInventoryReplicator;
import me.bingorufus.chatitemdisplay.util.string.StringFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DisplayParser {
    private final String s;
    private boolean containsItem;
    private boolean containsInventory;
    private boolean containsEnderchest;
    private DisplayItem item;
    private DisplayInventory inv;
    private DisplayInventory ec;

    public DisplayParser(String s) {
        this.s = s;
        read();
    }

    private void read() {
        for (String t : ChatItemConfig.ITEM_TRIGGERS) {
            if (s.contains(t)) {
                containsItem = true;
                break;
            }
        }
        for (String t : ChatItemConfig.INVENTORY_TRIGGERS) {
            if (s.contains(t)) {
                containsInventory = true;
                break;
            }
        }
        for (String t : ChatItemConfig.ENDERCHEST_TRIGGERS) {
            if (s.contains(t)) {
                containsEnderchest = true;
                break;
            }
        }
    }

    public boolean containsDisplay() {
        return containsItem || containsInventory || containsEnderchest;
    }

    public boolean containsItem() {
        return containsItem;
    }

    public boolean containsInventory() {
        return containsInventory;
    }

    public boolean containsEnderChest() {
        return containsEnderchest;
    }

    public String format(Player p) {
        if (item == null) createDisplayables(p);
        String out = s;
        for (String t : ChatItemConfig.ITEM_TRIGGERS) {
            if (!out.contains(t)) continue;
            ChatItemDisplay.getInstance().getDisplayedManager().addDisplayable(p.getName(), item);
            String ins = ChatItemDisplay.getInstance().getDisplayedManager().getDisplay(item).getInsertion();
            out = out.replace(t, ins);
        }
        for (String t : ChatItemConfig.INVENTORY_TRIGGERS) {
            if (!out.contains(t)) continue;

            ChatItemDisplay.getInstance().getDisplayedManager().addDisplayable(p.getName(), inv);
            String ins = ChatItemDisplay.getInstance().getDisplayedManager().getDisplay(inv).getInsertion();
            out = out.replace(t, ins);
        }
        for (String t : ChatItemConfig.ENDERCHEST_TRIGGERS) {
            if (!out.contains(t)) continue;
            ChatItemDisplay.getInstance().getDisplayedManager().addDisplayable(p.getName(), ec);
            String ins = ChatItemDisplay.getInstance().getDisplayedManager().getDisplay(ec).getInsertion();
            out = out.replace(t, ins);
        }
        return out;
    }

    public void createDisplayables(Player p) {
        ItemStack item = p.getInventory().getItemInMainHand();
        this.item = new DisplayItem(item, p.getName(), p.getDisplayName(), p.getUniqueId(), false);
        PlayerInventoryReplicator.InventoryData data = new PlayerInventoryReplicator().replicateInventory(p);
        inv = new DisplayInventory(data.getInventory(), data.getTitle(), p.getName(), p.getDisplayName(), p.getUniqueId(), false);
        String title = new StringFormatter().format(ChatItemConfig.ENDERCHEST_TITLE.replace("%player%",
                ChatItemDisplay.getInstance().getConfig().getBoolean("use-nicks-in-gui")
                        ? ChatItemDisplay.getInstance().getConfig().getBoolean("strip-nick-colors-gui")
                        ? ChatColor.stripColor(p.getDisplayName())
                        : p.getDisplayName()
                        : p.getName()));
        Inventory inv = Bukkit.createInventory(p, InventoryType.ENDER_CHEST, title);
        inv.setContents(p.getEnderChest().getContents());
        ec = new DisplayInventory(inv, title, p.getName(), p.getDisplayName(),
                p.getUniqueId(), false);
    }

    public Displayable getEnderChest() {
        return this.ec;
    }

    public Displayable getItem() {
        return this.item;
    }

    public Displayable getInventory() {
        return this.inv;
    }

}
