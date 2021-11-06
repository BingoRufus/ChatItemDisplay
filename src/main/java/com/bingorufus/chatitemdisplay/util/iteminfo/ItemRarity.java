package com.bingorufus.chatitemdisplay.util.iteminfo;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ItemRarity {
    COMMON(""), UNCOMMON("§e"), RARE("§b"), EPIC("§d");

    private final String color;

    ItemRarity(String color) {
        this.color = color;
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
            case SKULL_BANNER_PATTERN:
            case EXPERIENCE_BOTTLE:
            case DRAGON_BREATH:
            case ELYTRA:
            case CREEPER_HEAD:
            case PLAYER_HEAD:
            case DRAGON_HEAD:
            case ZOMBIE_HEAD:
            case SKELETON_SKULL:
            case WITHER_SKELETON_SKULL:
            case HEART_OF_THE_SEA:
            case NETHER_STAR:
            case TOTEM_OF_UNDYING:
            case ENCHANTED_BOOK:
                return UNCOMMON;

            case BEACON:
            case CONDUIT:
            case END_CRYSTAL:
                return RARE;

            case MOJANG_BANNER_PATTERN:
            case COMMAND_BLOCK:
            case DRAGON_EGG:
            case STRUCTURE_BLOCK:
            case JIGSAW:
                return EPIC;
            default:
                return COMMON;

        }


    }

    public String getColor() {
        return color;
    }


}
