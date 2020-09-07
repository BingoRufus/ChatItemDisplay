package me.bingorufus.chatitemdisplay.displayables;

import java.util.UUID;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import com.google.gson.JsonObject;

import me.bingorufus.chatitemdisplay.util.iteminfo.InventorySerializer;

public class DisplayInventory implements Displayable {

	private Inventory inv;
	private String name;
	private String player;
	private String displayName;
	private UUID uuid;
	private boolean fromBungee;
	private DisplayType type;

	public DisplayInventory(Inventory inv, String inventoryName, String player, String displayName,
			UUID uuid,
			boolean fromBungee) {


		this.inv = inv;
		this.name = inventoryName;
		this.player = player;
		this.displayName = displayName;
		this.uuid = uuid;
		this.fromBungee = fromBungee;
		if (inv.getType() == InventoryType.ENDER_CHEST)
			type = DisplayType.ENDERCHEST;
		else
			type = DisplayType.INVENTORY;
	}

	@Override
	public String serialize() {
		JsonObject json = new JsonObject();
		json.addProperty("inventory", new InventorySerializer().serialize(inv, name));
		json.addProperty("title", name);
		json.addProperty("player", player);
		json.addProperty("displayName", displayName);
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

	public Inventory getInventory() {
		return inv;
	}

	@Override
	public boolean fromBungee() {
		return fromBungee;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public DisplayType getType() {
		return type;
	}


}
