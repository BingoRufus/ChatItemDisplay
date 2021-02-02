package com.github.bingorufus.chatitemdisplay.util.iteminfo;

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

    public String serialize(Inventory inv, @Nullable String name) {
        if (name == null)
            return serialize(inv);

        JsonObject invJson = new JsonObject();
        if (inv.getType().getDefaultSize() == inv.getSize())
            invJson.addProperty("type", inv.getType().name());
        else
            invJson.addProperty("size", inv.getSize());

        invJson.addProperty("owner", inv.getHolder() != null ? ((Player) inv.getHolder()).getUniqueId().toString() : "");

        invJson.addProperty("title", name);

        invJson.add("contents", getContents(inv));
        return invJson.toString();

    }

    public String serialize(Inventory inv) {
        JsonObject invJson = new JsonObject();
        if (inv.getType().getDefaultSize() == inv.getSize())
            invJson.addProperty("type", inv.getType().name());
        else
            invJson.addProperty("size", inv.getSize());

        invJson.addProperty("owner", inv.getHolder() != null ? ((Player) inv.getHolder()).getUniqueId().toString() : "");
        invJson.add("contents", getContents(inv));
        return invJson.toString();

    }

    private JsonElement getContents(@NotNull Inventory inv) {
        JsonObject con = new JsonObject();
        ItemSerializer serializer = new ItemSerializer();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getItemMeta() == null)
                continue;
            con.addProperty(i + "", serializer.serialize(item));
        }
        return con;
    }

    public Inventory deserialize(String json) {
        JsonObject invJson = (JsonObject) new JsonParser().parse(json);
        OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(invJson.get("owner").getAsString()));
        String title = invJson.has("title") ? invJson.get("title").getAsString() : null;
        Inventory inv = null;
        if (invJson.has("type")) {
            InventoryType type = InventoryType.valueOf(invJson.get("type").getAsString());
            inv = Bukkit.createInventory(owner.getPlayer(), type, title == null ? type.getDefaultTitle() : title);
        } else if (invJson.has("size")) {
            inv = Bukkit.createInventory(owner.getPlayer(), invJson.get("size").getAsInt(),
                    title == null ? "Inventory" : title);
        }

        JsonObject items = invJson.get("contents").getAsJsonObject();
        ItemSerializer serialzer = new ItemSerializer();
        if (inv == null) return null;
        for (int i = 0; i < inv.getSize(); i++) {
            if (!items.has(i + ""))
                continue;

            inv.setItem(i, serialzer.deserialize(items.get(i + "").getAsString()));
        }
        return inv;


    }

}
