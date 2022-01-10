package com.bingorufus.chatitemdisplay.util.iteminfo.reflection;

import com.bingorufus.chatitemdisplay.util.ReflectionClassRetriever;

public class ReflectionClasses {
    public static final Class<?> craftPotionUtil = ReflectionClassRetriever.getCraftBukkitClassOrThrow("potion.CraftPotionUtil");
    public static final Class<?> craftItemStack = ReflectionClassRetriever.getCraftBukkitClassOrThrow("inventory.CraftItemStack");
    public static final Class<?> chatSerializer = ReflectionClassRetriever.getNMSClassOrThrow("network.chat.IChatBaseComponent$ChatSerializer");
    public static final Class<?> iChatBaseComponent = ReflectionClassRetriever.getNMSClassOrThrow("network.chat.IChatBaseComponent");
    public static final Class<?> nbtTagCompound = ReflectionClassRetriever.getNMSClassOrThrow("nbt.NBTTagCompound");
    public static final Class<?> nbtTagList = ReflectionClassRetriever.getNMSClassOrThrow("nbt.NBTTagList");
    public static final Class<?> nbtTagString = ReflectionClassRetriever.getNMSClassOrThrow("nbt.NBTTagString");
    public static final Class<?> nbtBase = ReflectionClassRetriever.getNMSClassOrThrow("nbt.NBTBase");

    public static final Class<?> nmsItemStack = ReflectionClassRetriever.getNMSClassOrThrow("world.item.ItemStack");
    public static final Class<?> nmsItemWorld = ReflectionClassRetriever.getNMSClassOrThrow("world.item.Item");

}
