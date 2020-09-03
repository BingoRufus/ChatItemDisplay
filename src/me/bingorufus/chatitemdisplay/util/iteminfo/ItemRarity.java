package me.bingorufus.chatitemdisplay.util.iteminfo;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ItemRarity {
	COMMON(""), UNCOMMON("§e"), RARE("§b"), EPIC("§d");

	private final String color;
	private ItemRarity(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public static ItemRarity getRarity(ItemStack item) {
		ItemRarity baseRarity = itemRarity(item);
		if (!(item.getEnchantments().size() > 0)) // If item is enchanted
			return baseRarity;
		if (baseRarity.equals(RARE) || baseRarity.equals(EPIC))
			return EPIC;
		return RARE;
	}

	private static ItemRarity itemRarity(ItemStack item) { // This info came from here
															// https://minecraft.gamepedia.com/Rarity
		Material m = item.getType();
		if (m.isRecord())
			return RARE;

		switch (item.getType()) {
		case CREEPER_BANNER_PATTERN:
			return UNCOMMON;
		case SKULL_BANNER_PATTERN:
			return UNCOMMON;
		case EXPERIENCE_BOTTLE:
			return UNCOMMON;
		case DRAGON_BREATH:
			return UNCOMMON;
		case ELYTRA:
			return UNCOMMON;
		case CREEPER_HEAD:
			return UNCOMMON;
		case DRAGON_HEAD:
			return UNCOMMON;
		case PLAYER_HEAD:
			return UNCOMMON;
		case ZOMBIE_HEAD:
			return UNCOMMON;
		case SKELETON_SKULL:
			return UNCOMMON;
		case WITHER_SKELETON_SKULL:
			return UNCOMMON;
		case HEART_OF_THE_SEA:
			return UNCOMMON;
		case NETHER_STAR:
			return UNCOMMON;
		case TOTEM_OF_UNDYING:
			return UNCOMMON;
		case ENCHANTED_BOOK:
			return UNCOMMON;

		case BEACON:
			return RARE;
		case CONDUIT:
			return RARE;
		case END_CRYSTAL:
			return RARE;

		case MOJANG_BANNER_PATTERN:
			return EPIC;
		case COMMAND_BLOCK:
			return EPIC;
		case DRAGON_EGG:
			return EPIC;
		case STRUCTURE_BLOCK:
			return EPIC;
		case JIGSAW:
			return EPIC;
		default:
			return COMMON;

		}


	}




}
