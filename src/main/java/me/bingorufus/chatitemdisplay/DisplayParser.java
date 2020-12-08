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
        String out = s;
        ItemStack item = p.getInventory().getItemInMainHand();
        for (String t : ChatItemConfig.ITEM_TRIGGERS) {
            if (!out.contains(t)) continue;
            Displayable di = new DisplayItem(item, p.getName(), p.getDisplayName(), p.getUniqueId(), false);
            ChatItemDisplay.getInstance().getDisplayedManager().addDisplayable(p.getName(), di);
            String ins = ChatItemDisplay.getInstance().getDisplayedManager().getDisplay(di).getInsertion();
            out = out.replace(t, ins);
        }
        for (String t : ChatItemConfig.INVENTORY_TRIGGERS) {
            if (!out.contains(t)) continue;
            PlayerInventoryReplicator.InventoryData data = new PlayerInventoryReplicator().replicateInventory(p);
            Displayable di = new DisplayInventory(data.getInventory(), data.getTitle(), p.getName(), p.getDisplayName(), p.getUniqueId(), false);
            ChatItemDisplay.getInstance().getDisplayedManager().addDisplayable(p.getName(), di);
            String ins = ChatItemDisplay.getInstance().getDisplayedManager().getDisplay(di).getInsertion();
            out = out.replace(t, ins);
        }
        for (String t : ChatItemConfig.ENDERCHEST_TRIGGERS) {
            if (!out.contains(t)) continue;

            String title = new StringFormatter().format(ChatItemConfig.ENDERCHEST_TITLE.replace("%player%",
                    ChatItemDisplay.getInstance().getConfig().getBoolean("use-nicks-in-gui")
                            ? ChatItemDisplay.getInstance().getConfig().getBoolean("strip-nick-colors-gui")
                            ? ChatColor.stripColor(p.getDisplayName())
                            : p.getDisplayName()
                            : p.getName()));
            Inventory inv = Bukkit.createInventory(p, InventoryType.ENDER_CHEST, title);
            inv.setContents(p.getEnderChest().getContents());
            Displayable di = new DisplayInventory(inv, title, p.getName(), p.getDisplayName(),
                    p.getUniqueId(), false);
            ChatItemDisplay.getInstance().getDisplayedManager().addDisplayable(p.getName(), di);
            String ins = ChatItemDisplay.getInstance().getDisplayedManager().getDisplay(di).getInsertion();
            out = out.replace(t, ins);
        }
        return out;
    }


}
