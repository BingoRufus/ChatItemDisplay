package com.bingorufus.chatitemdisplay.util.iteminfo.reflection;

import com.bingorufus.chatitemdisplay.util.ReflectionClassRetriever;

public class ReflectionClasses {
    public static final Class<?> craftPotionUtil = ReflectionClassRetriever.getCraftBukkitClassOrThrow("potion.CraftPotionUtil");
    public static final Class<?> craftItemStack = ReflectionClassRetriever.getCraftBukkitClassOrThrow("inventory.CraftItemStack");
    public static final Class<?> chatSerializer = ReflectionClassRetriever.getNMSClassOrThrow("IChatBaseComponent$ChatSerializer");
    public static final Class<?> iChatBaseComponent = ReflectionClassRetriever.getNMSClassOrThrow("IChatBaseComponent");
    public static final Class<?> nbtTagCompound = ReflectionClassRetriever.getNMSClassOrThrow("NBTTagCompound");
    public static final Class<?> nbtTagList = ReflectionClassRetriever.getNMSClassOrThrow("NBTTagList");
    public static final Class<?> nbtTagString = ReflectionClassRetriever.getNMSClassOrThrow("NBTTagString");
    public static final Class<?> nbtBase = ReflectionClassRetriever.getNMSClassOrThrow("NBTBase");
    public static final Class<?> nmsItemStack = ReflectionClassRetriever.getNMSClassOrThrow("ItemStack");
}
