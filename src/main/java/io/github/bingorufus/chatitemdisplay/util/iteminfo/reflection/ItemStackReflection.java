package io.github.bingorufus.chatitemdisplay.util.iteminfo.reflection;

import io.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import io.github.bingorufus.chatitemdisplay.util.string.VersionComparator;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.ItemStack;

public class ItemStackReflection {
    private static final ReflectionInterface REFLECTION_INTERFACE;

    static {
        REFLECTION_INTERFACE = VersionComparator.isRecent(ChatItemDisplay.MINECRAFT_VERSION, "1.17") == VersionComparator.Status.BEHIND ? new Pre17ItemStackReflection() : new Post17ItemStackReflection();
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

    public static ItemStack setItemName(final ItemStack item, final BaseComponent name) {
        return REFLECTION_INTERFACE.setItemName(item, name);
    }

    public static ItemStack setLore(final ItemStack item, final BaseComponent... lore) {
        return REFLECTION_INTERFACE.setLore(item, lore);
    }


}
