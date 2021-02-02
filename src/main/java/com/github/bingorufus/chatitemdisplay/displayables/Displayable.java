package com.github.bingorufus.chatitemdisplay.displayables;

import com.github.bingorufus.chatitemdisplay.util.iteminfo.InventorySerializer;
import com.github.bingorufus.chatitemdisplay.util.iteminfo.ItemSerializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.UUID;

public interface Displayable {

    static Displayable deserialize(String json) {
        JsonObject displayJson = (JsonObject) new JsonParser().parse(json);

        if (displayJson.has("item")) {

            return new DisplayItem(new ItemSerializer().deserialize(displayJson.get("item").getAsString()),
                    displayJson.get("player").getAsString(), displayJson.get("displayName").getAsString(),
                    UUID.fromString(displayJson.get("uuid").getAsString())
            );
        } else {

            return new DisplayInventory(
                    new InventorySerializer().deserialize(displayJson.get("inventory").getAsString()),
                    displayJson.get("title").getAsString(), displayJson.get("player").getAsString(),
                    displayJson.get("displayName").getAsString(),
                    UUID.fromString(displayJson.get("uuid").getAsString()));
        }

    }

    String getPlayer();

    UUID getUUID();

    String serialize();

    String getDisplayName();

    default DisplayInfo getInfo() {
        if (this instanceof DisplayInventory) {
            return new DisplayInventoryInfo((DisplayInventory) this);
        }
        return new DisplayItemInfo((DisplayItem) this);

    }

    default DisplayType getType() {
        if (this instanceof DisplayInventory) {
            return this.getType();
        }
        return DisplayType.ITEM;
    }


}
