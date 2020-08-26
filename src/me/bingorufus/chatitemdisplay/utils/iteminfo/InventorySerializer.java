package me.bingorufus.chatitemdisplay.utils.iteminfo;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class InventorySerializer {

	public String serialize(Inventory inv, String name) {
		JsonObject invJson = new JsonObject();
		invJson.addProperty("type", inv.getType().name());
		invJson.addProperty("owner", inv.getHolder().toString());

		invJson.addProperty("title", name);

		invJson.add("contents", getContents(inv));
		return invJson.toString();

	}

	public String serialize(Inventory inv) {
		JsonObject invJson = new JsonObject();
		invJson.addProperty("type", inv.getType().name());
		invJson.addProperty("owner", inv.getHolder().toString());
		invJson.add("contents", getContents(inv));
		return invJson.toString();

	}

	private JsonElement getContents(Inventory inv) {
		JsonObject con = new JsonObject();
		ItemSerializer serializer = new ItemSerializer();
		for (int i = 0; i < inv.getType().getDefaultSize(); i++) {
			ItemStack item = inv.getItem(i);
			if (item == null || item.getItemMeta() == null)
				continue;
				con.addProperty(i + "", serializer.serialize(inv.getItem(i)));
		}
		return con;
	}

	public Inventory deserialize(String json) {
		JsonObject invJson = (JsonObject) new JsonParser().parse(json);

		Inventory inv = Bukkit.createInventory(
				(InventoryHolder) Bukkit.getOfflinePlayer(UUID.fromString(invJson.get("owner").getAsString())),
				InventoryType.valueOf(invJson.get("type").getAsString()),
				invJson.has("title") ? invJson.get("title").getAsString()
						: InventoryType.valueOf(invJson.get("type").getAsString()).getDefaultTitle());

		JsonObject items = invJson.get("contents").getAsJsonObject();
		ItemSerializer serialzer = new ItemSerializer();
		for(int i = 0; i < inv.getType().getDefaultSize(); i++) {
			if (!items.has(i + ""))
				continue;
			inv.setItem(i, serialzer.deserialize(invJson.get(i + "").getAsJsonObject().toString()));
		}
		return inv;
		
		

	}

}
