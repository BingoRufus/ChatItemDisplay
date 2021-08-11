package io.github.bingorufus.chatitemdisplay.util.iteminfo.reflection;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.ItemStack;

public interface ReflectionInterface {
    BaseComponent getOldHover(ItemStack item);

    boolean hasNbt(ItemStack item);

    String getNBT(ItemStack item);

    String translateItemStack(ItemStack holding);

    ItemStack setItemName(final ItemStack item, final BaseComponent name);

    ItemStack setLore(final ItemStack item, final BaseComponent... lore);
}
