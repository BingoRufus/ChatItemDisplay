package com.github.bingorufus.chatitemdisplay;

import com.github.bingorufus.chatitemdisplay.api.event.DisplayPreProcessEvent;
import com.github.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import com.github.bingorufus.chatitemdisplay.displayables.DisplayItem;
import com.github.bingorufus.chatitemdisplay.displayables.DisplayType;
import com.github.bingorufus.chatitemdisplay.displayables.Displayable;
import com.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.github.bingorufus.chatitemdisplay.util.iteminfo.PlayerInventoryReplicator;
import com.github.bingorufus.chatitemdisplay.util.string.StringFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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
        if (item == null && ec == null && inv == null) createDisplayables(p);
        String out = s;
        out = replaceTrigger(out, p, DisplayType.ITEM);
        out = replaceTrigger(out, p, DisplayType.INVENTORY);
        out = replaceTrigger(out, p, DisplayType.ENDERCHEST);

        return out;
    }

    private String replaceTrigger(String message, Player p, DisplayType type) {
        String out = message;
        Displayable displayable;
        List<String> triggers;
        boolean sentEvent = false;
        switch (type) {
            case INVENTORY:
                triggers = ChatItemConfig.INVENTORY_TRIGGERS;
                displayable = inv;
                break;
            case ENDERCHEST:
                triggers = ChatItemConfig.ENDERCHEST_TRIGGERS;
                displayable = ec;
                break;
            default:
                triggers = ChatItemConfig.ITEM_TRIGGERS;
                displayable = item;
                break;
        }
        for (String t : triggers) {
            if (!out.contains(t)) continue;
            if (!sentEvent) {
                DisplayPreProcessEvent displayEvent = new DisplayPreProcessEvent(p, displayable, true);
                Bukkit.getPluginManager().callEvent(displayEvent);
                if (displayEvent.isCancelled()) {
                    p.sendMessage(displayEvent.getCancellationMessage());
                    break;
                }
                sentEvent = true;
            }

            ChatItemDisplay.getInstance().getDisplayedManager().addDisplayable(p.getName(), displayable);
            String ins = ChatItemDisplay.getInstance().getDisplayedManager().getDisplay(displayable).getInsertion();
            out = out.replace(t, ins);
        }
        return out;

    }

    public void createDisplayables(Player p) {
        if (containsItem()) {
            ItemStack item = p.getInventory().getItemInMainHand();
            this.item = new DisplayItem(item, p.getName(), p.getDisplayName(), p.getUniqueId());
        }
        if (containsInventory()) {
            PlayerInventoryReplicator.InventoryData data = new PlayerInventoryReplicator().replicateInventory(p);
            inv = new DisplayInventory(data.getInventory(), data.getTitle(), p.getName(), p.getDisplayName(), p.getUniqueId());
        }
        if (containsEnderChest()) {
            String title = new StringFormatter().format(ChatItemConfig.ENDERCHEST_TITLE.replace("%player%",
                    ChatItemDisplay.getInstance().getConfig().getBoolean("use-nicks-in-gui")
                            ? ChatItemDisplay.getInstance().getConfig().getBoolean("strip-nick-colors-gui")
                            ? ChatColor.stripColor(p.getDisplayName())
                            : p.getDisplayName()
                            : p.getName()));
            Inventory inv = Bukkit.createInventory(p, InventoryType.ENDER_CHEST, title);
            inv.setContents(p.getEnderChest().getContents());
            ec = new DisplayInventory(inv, title, p.getName(), p.getDisplayName(),
                    p.getUniqueId());
        }
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
