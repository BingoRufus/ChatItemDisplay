package me.bingorufus.chatitemdisplay.displayables;

import java.io.File;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.util.iteminfo.InventorySerializer;
import me.bingorufus.chatitemdisplay.util.iteminfo.ItemSerializer;

public interface Displayable {

	public boolean fromBungee();

	public String getPlayer();

	public UUID getUUID();

	public String serialize();

	public String getDisplayName();

	public File getImage();


	default DisplayInfo getInfo() {
		if (this instanceof DisplayInventory) {
			return new DisplayInventoryInfo(JavaPlugin.getPlugin(ChatItemDisplay.class), (DisplayInventory) this);
		}
		return new DisplayItemInfo(JavaPlugin.getPlugin(ChatItemDisplay.class), (DisplayItem) this);

	}

	default DisplayType getType() {
		if (this instanceof DisplayInventory) {
			return ((DisplayInventory) this).getType();
		}
		return DisplayType.ITEM;
	}


	public static Displayable deserialize(String json) {
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


}
