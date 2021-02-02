package com.github.bingorufus.chatitemdisplay.displayables;

import com.github.bingorufus.chatitemdisplay.util.iteminfo.ItemSerializer;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DisplayItem implements Displayable {
    @Getter
    private final String player;
    @Getter
    private final String displayName;
    @Getter
    private final UUID UUID;
    @Getter
    @Setter
    private ItemStack item;

    public DisplayItem(ItemStack item, String player, String displayName, UUID uuid) {
        this.item = item.clone();
        this.player = player;
        this.displayName = displayName;
        this.UUID = uuid;

    }

    @Override
    public String serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("item", new ItemSerializer().serialize(item));
        json.addProperty("player", player);
        json.addProperty("displayName", getDisplayName());
        json.addProperty("uuid", UUID.toString());
        json.addProperty("bungee", true);


        return json.toString();
    }


}
