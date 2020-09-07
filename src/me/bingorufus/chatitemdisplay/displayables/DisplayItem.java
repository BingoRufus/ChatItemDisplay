package me.bingorufus.chatitemdisplay.displayables;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import me.bingorufus.chatitemdisplay.util.iteminfo.ItemSerializer;

public class DisplayItem implements Displayable {
	private ItemStack item;
	private String player;
	private String displayName;
	private UUID uuid;
	private boolean fromBungee;

	public DisplayItem(ItemStack item, String player, String displayName, UUID uuid, boolean fromBungee) {
		this.item = item;
		this.player = player;
		this.displayName = displayName;
		this.uuid = uuid;
		this.fromBungee = fromBungee;

	}

	@Override
	public String serialize() {
		JsonObject json = new JsonObject();
		json.addProperty("item", new ItemSerializer().serialize(item));
		json.addProperty("player", player);
		json.addProperty("displayName", getDisplayName());
		json.addProperty("uuid", uuid.toString());
		json.addProperty("bungee", true);


		return json.toString();
	}



	@Override
	public String getPlayer() {
		return player;
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}


	public ItemStack getItem() {
		return item;
	}

	@Override
	public boolean fromBungee() {
		return this.fromBungee;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}


}
