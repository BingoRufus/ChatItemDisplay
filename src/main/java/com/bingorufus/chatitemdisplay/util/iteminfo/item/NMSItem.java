package com.bingorufus.chatitemdisplay.util.iteminfo.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;

import static com.bingorufus.chatitemdisplay.util.iteminfo.reflection.ReflectionClasses.craftItemStack;
import static com.bingorufus.chatitemdisplay.util.iteminfo.reflection.ReflectionClasses.iChatBaseComponent;

public abstract class NMSItem {


    private Object nmsItem;

    public NMSItem(ItemStack itemStack) {
        nmsItem = toNmsItem(itemStack);
    }

    private static @Nullable Object toNmsItem(ItemStack item) {
        try {
            Method asNms = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
            return asNms.invoke(craftItemStack, item);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract NMSNBTTag getTag();

    public abstract void setTag(NMSNBTTag tag);

    public abstract boolean hasNBT();

    public void setItemName(NMSChatTag name) {
        try {
            Method setName = Arrays.stream(nmsItem.getClass().getDeclaredMethods()).filter(method -> method.getReturnType().equals(nmsItem.getClass())).filter(method -> method.getParameterCount() == 1).filter(method -> method.getParameterTypes()[0].equals(iChatBaseComponent)).findFirst().orElseThrow(() -> new NoSuchMethodException("Could not obtain the setName method"));

            nmsItem = setName.invoke(nmsItem, name.getNmsTag());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public ItemStack convertToBukkitItemStack() {
        try {
            Method fromNMS = craftItemStack.getMethod("asBukkitCopy", nmsItem.getClass());
            fromNMS.setAccessible(true);
            return (ItemStack) fromNMS.invoke(craftItemStack, nmsItem);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }


}
