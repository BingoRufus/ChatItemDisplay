package com.github.bingorufus.chatitemdisplay.displayables;

import com.github.bingorufus.chatitemdisplay.util.iteminfo.InventorySerializer;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class DisplayInventory implements Displayable {

    @Getter
    private final Inventory inventory;
    private final String inventoryName;
    @Getter
    private final String player;
    @Getter
    private final String displayName;
    @Getter
    private final UUID UUID;
    @Getter
    private final DisplayType type;

    public DisplayInventory(Inventory inventory, String inventoryName, String player, String displayName,
                            UUID UUID) {
        this.inventory = inventory;
        this.inventoryName = inventoryName;
        this.player = player;
        this.displayName = displayName;
        this.UUID = UUID;
        if (inventory.getType() == InventoryType.ENDER_CHEST)
            type = DisplayType.ENDERCHEST;
        else
            type = DisplayType.INVENTORY;
    }

    @Override
    public String serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("inventory", new InventorySerializer().serialize(inventory, inventoryName));
        json.addProperty("title", inventoryName);
        json.addProperty("player", player);
        json.addProperty("displayName", displayName);
        json.addProperty("uuid", UUID.toString());
        json.addProperty("bungee", true);
        return json.toString();
    }


}
