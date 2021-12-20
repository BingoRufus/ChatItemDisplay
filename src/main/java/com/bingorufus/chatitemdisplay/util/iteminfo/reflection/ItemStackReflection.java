package com.bingorufus.chatitemdisplay.util.iteminfo.reflection;

import com.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.bingorufus.chatitemdisplay.util.string.VersionComparator;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

public class ItemStackReflection {
    private static final ReflectionInterface REFLECTION_INTERFACE;

    static {
        if (VersionComparator.isRecent(ChatItemDisplay.MINECRAFT_VERSION, "1.18") == VersionComparator.Status.BEHIND) {
            if (VersionComparator.isRecent(ChatItemDisplay.MINECRAFT_VERSION, "1.17") == VersionComparator.Status.BEHIND) {
                REFLECTION_INTERFACE = new Pre17ItemStackReflection();
            } else {
                REFLECTION_INTERFACE = new Release17ItemStackReflection();
            }

        } else {
            REFLECTION_INTERFACE = new Post17ItemStackReflection();
        }

    }

    public static BaseComponent getOldHover(ItemStack item) {
        return REFLECTION_INTERFACE.getOldHover(item);

    }

    public static boolean hasNbt(ItemStack item) {
        return REFLECTION_INTERFACE.hasNbt(item);
    }

    public static String getNBT(ItemStack item) {
        return REFLECTION_INTERFACE.getNBT(item);
    }

    public static String translateItemStack(ItemStack holding) {
        return REFLECTION_INTERFACE.translateItemStack(holding);
    }

    public static TextComponent translateItemStackComponent(ItemStack holding) {
        return REFLECTION_INTERFACE.translateItemStackComponent(holding);
    }

    public static ItemStack setItemName(final ItemStack item, final BaseComponent name) {
        return REFLECTION_INTERFACE.setItemName(item, name);
    }

    public static ItemStack setLore(final ItemStack item, final BaseComponent... lore) {
        return REFLECTION_INTERFACE.setLore(item, lore);
    }


}
