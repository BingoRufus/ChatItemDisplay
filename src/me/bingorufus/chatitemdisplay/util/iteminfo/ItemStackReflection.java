package me.bingorufus.chatitemdisplay.util.iteminfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class ItemStackReflection {

	private Class<?> craftPotionUtil;
	private Class<?> craftItemStack;
	private Class<?> chatSerializer;
	private Class<?> iChatBase;


	
	public ItemStackReflection() {
		try {
			String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			craftPotionUtil = Class
					.forName("org.bukkit.craftbukkit.{v}.potion.CraftPotionUtil".replace("{v}", version));

			craftItemStack = Class
					.forName("org.bukkit.craftbukkit.{v}.inventory.CraftItemStack".replace("{v}", version));
			chatSerializer = Class
					.forName("net.minecraft.server.{v}.IChatBaseComponent$ChatSerializer".replace("{v}", version));
			iChatBase = Class.forName("net.minecraft.server.{v}.IChatBaseComponent".replace("{v}", version));

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

	public BaseComponent getOldHover(ItemStack item) {
		try {
			Object nmsItem = nmsItem(item);
			Method getChatComponent = nmsItem.getClass().getMethod("B");
			Object chatComponent = getChatComponent.invoke(nmsItem);

			Method serialze = chatSerializer.getMethod("a", iChatBase);
			String s = (String) serialze.invoke(chatSerializer, iChatBase.cast(chatComponent));
			return ComponentSerializer.parse(s)[0];
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {

		}
		 
		return new TextComponent();
	}

	public boolean hasNbt(ItemStack item) {
		try {
			Object nmsItem = nmsItem(item);
			if (nmsItem == null) {
				throw new IllegalArgumentException(
						item.getType().name() + " could not be turned into a net.minecraft item");
			}
			Method hasTag = nmsItem.getClass().getMethod("hasTag");


			return(boolean) hasTag.invoke(nmsItem);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			return false;
		}

	}

	public String getNBT(ItemStack item) {
		try {
			Object nmsItem = nmsItem(item);
			if (nmsItem == null) {
				throw new IllegalArgumentException(
						item.getType().name() + " could not be turned into a net.minecraft item");
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


	public String translateItemStack(ItemStack holding) {
		try {
			Object item = nmsItem(holding);
			if (item == null) {
				throw new IllegalArgumentException(holding.getType().name() + " could not be queried!");
			}
			Method getItem = item.getClass().getMethod("getItem");
			getItem.setAccessible(true);
			Object newItem = getItem.invoke(item);
			Method getName = newItem.getClass().getMethod("getName");
			getName.setAccessible(true);
			String key = (String) getName.invoke(newItem);
			if (holding.getItemMeta() instanceof PotionMeta) {
				PotionMeta pm = (PotionMeta) holding.getItemMeta();

				key += ".effect.";
				Method fromBukkit = craftPotionUtil.getMethod("fromBukkit", PotionData.class);
				fromBukkit.setAccessible(true);
				String potionkey = (String) fromBukkit.invoke(craftPotionUtil,
						new PotionData(pm.getBasePotionData().getType()));
				potionkey = potionkey.replaceAll("minecraft:", "");
				key += potionkey;

			}

			return key;

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return "";

	}



}
