package me.bingorufus.chatitemdisplay.util.iteminfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ItemSerializer {
	private Class<?> craftItemStack;
	private Class<?> mojangsonParser;
	private Class<?> nmsItemStack;

	public ItemSerializer() {
		try {
			String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

			craftItemStack = Class
					.forName("org.bukkit.craftbukkit.{v}.inventory.CraftItemStack".replace("{v}", version));

			mojangsonParser = Class.forName("net.minecraft.server.{v}.MojangsonParser".replace("{v}", version));

			nmsItemStack = Class.forName("net.minecraft.server.{v}.ItemStack".replace("{v}", version));

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	private Object nmsItem(ItemStack item) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {

		Method asNms = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
		asNms.setAccessible(true);
		Object nmsItem = asNms.invoke(craftItemStack, item);

		return nmsItem;

	}

	public String serialize(ItemStack item) {
		JsonObject itemJson = new JsonObject();
		itemJson.addProperty("id", item.getType().getKey().toString());
		itemJson.addProperty("Count", item.getAmount());
		itemJson.addProperty("tag", getNBT(item));
		return itemJson.toString();
	}

	public ItemStack deserialize(String json) {
		JsonObject itemJson = (JsonObject) new JsonParser().parse(json);
		Material mat = Material.matchMaterial(itemJson.get("id").getAsString());
		mat = mat == null ? Material.STONE : mat; // If the item type does not exist (If the version changed)

		int count = itemJson.get("Count").getAsInt();

		ItemStack item = new ItemStack(mat, count);
		try {

			Object nmsItem = nmsItem(item);

			Method parseNBT = mojangsonParser.getMethod("parse", String.class);

			Object nbtCompound = parseNBT.invoke(mojangsonParser, itemJson.get("tag").getAsString()); // Turns
																														// NBT
																										// string into
																										// NBTTagCompound

			Method setTag = nmsItemStack.getMethod("setTag", nbtCompound.getClass());
			setTag.invoke(nmsItem, nbtCompound);

			Method asBukkitCopy = craftItemStack.getMethod("asBukkitCopy", nmsItemStack);
			ItemStack editedItem = (ItemStack) asBukkitCopy.invoke(craftItemStack, nmsItem);
			return editedItem;

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return item;

	}

	private String getNBT(ItemStack item) {

		try {
			Object nmsItem = nmsItem(item);
			if (nmsItem == null) {
				throw new IllegalArgumentException(item.getType().name() + " could not be converted to NMS");
			}
			Method hasTag = nmsItem.getClass().getMethod("hasTag");

			if ((boolean) hasTag.invoke(nmsItem)) {
				Method getTag = nmsItem.getClass().getMethod("getTag");
				Object nbtData = getTag.invoke(nmsItem);
				Method asString = nbtData.getClass().getMethod("asString");
				return (String) asString.invoke(nbtData);
			}

		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return "{}";

	}

}
