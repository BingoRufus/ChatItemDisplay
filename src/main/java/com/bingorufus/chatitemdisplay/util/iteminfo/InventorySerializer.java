package com.bingorufus.chatitemdisplay.util.iteminfo;

import com.bingorufus.chatitemdisplay.util.iteminfo.reflection.ItemSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public class InventorySerializer {
    private InventorySerializer() {
    }

    @NotNull
    public static Inventory cloneInventory(Inventory inventory, String inventoryTitle) {
        Inventory inventoryClone;
        if (inventory.getType() == InventoryType.CHEST) {
            inventoryClone = Bukkit.createInventory(null, inventory.getSize(), inventoryTitle);
        } else {
            inventoryClone = Bukkit.createInventory(null, inventory.getType(), inventoryTitle);
        }
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null) inventoryClone.setItem(i, inventory.getItem(i).clone());
        }

        return inventory;
    }

    public static String serialize(Inventory inv, @Nullable String name) {
        if (name == null)
            return serialize(inv);
        JsonObject invJson = serializeInventory(inv);

        invJson.addProperty("title", name);


        return invJson.toString();

    }

    private static JsonObject serializeInventory(Inventory inv) {
        JsonObject invJson = new JsonObject();
        if (inv.getType().getDefaultSize() == inv.getSize())
            invJson.addProperty("type", inv.getType().name());
        else
            invJson.addProperty("size", inv.getSize());

        invJson.addProperty("owner", inv.getHolder() != null ? ((Player) inv.getHolder()).getUniqueId().toString() : "");
        invJson.add("contents", getContents(inv));
        return invJson;
    }

    private static JsonElement getContents(@NotNull Inventory inv) {
        JsonObject con = new JsonObject();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getItemMeta() == null)
                continue;
            con.addProperty(i + "", ItemSerializer.serialize(item));
        }
        return con;
    }

    public static String serialize(Inventory inv) {
        return serializeInventory(inv).toString();

    }

    public static InventoryData deserialize(String json) {
        JsonObject invJson = (JsonObject) new JsonParser().parse(json);
        OfflinePlayer owner = invJson.get("owner").getAsString().equals("") ? null : Bukkit.getOfflinePlayer(UUID.fromString(invJson.get("owner").getAsString()));
        String title = invJson.has("title") ? invJson.get("title").getAsString() : null;
        Inventory inv = null;
        if (invJson.has("type")) {
            InventoryType type = InventoryType.valueOf(invJson.get("type").getAsString());
            inv = Bukkit.createInventory(owner == null ? null : owner.getPlayer(), type, title == null ? type.getDefaultTitle() : title);
        } else if (invJson.has("size")) {
            inv = Bukkit.createInventory(owner == null ? null : owner.getPlayer(), invJson.get("size").getAsInt(),
                    title == null ? "Inventory" : title);
        }

        JsonObject items = invJson.get("contents").getAsJsonObject();
        if (inv == null) return null;
        for (int i = 0; i < inv.getSize(); i++) {
            if (!items.has(i + ""))
                continue;

            inv.setItem(i, ItemSerializer.deserialize(items.get(i + "").getAsString()));
        }
        return new InventoryData(inv, title);


    }

}
