package me.bingorufus.chatitemdisplay.displayables;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.bingorufus.chatitemdisplay.util.iteminfo.InventorySerializer;
import me.bingorufus.chatitemdisplay.util.iteminfo.ItemSerializer;

import java.io.File;
import java.util.UUID;

public interface Displayable {

    static Displayable deserialize(String json) {
        JsonObject displayJson = (JsonObject) new JsonParser().parse(json);

        if (displayJson.has("item")) {

            return new DisplayItem(new ItemSerializer().deserialize(displayJson.get("item").getAsString()),
                    displayJson.get("player").getAsString(), displayJson.get("displayName").getAsString(),
                    UUID.fromString(displayJson.get("uuid").getAsString()),
                    displayJson.get("bungee").getAsBoolean());
        } else {

            return new DisplayInventory(
                    new InventorySerializer().deserialize(displayJson.get("inventory").getAsString()),
                    displayJson.get("title").getAsString(), displayJson.get("player").getAsString(),
                    displayJson.get("displayName").getAsString(),
                    UUID.fromString(displayJson.get("uuid").getAsString()), displayJson.get("bungee").getAsBoolean());
        }

    }

    boolean fromBungee();

    String getPlayer();

    UUID getUUID();

    String serialize();

    String getDisplayName();

    File getImage();

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
