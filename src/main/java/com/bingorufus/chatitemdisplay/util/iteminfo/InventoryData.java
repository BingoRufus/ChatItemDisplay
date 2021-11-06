package com.bingorufus.chatitemdisplay.util.iteminfo;

import lombok.Getter;
import org.bukkit.inventory.Inventory;

public class InventoryData {
    @Getter
    private final Inventory inventory;
    @Getter
    private final String title;

    public InventoryData(Inventory inventory, String title) {
        this.inventory = inventory;
        this.title = title;
    }
}
