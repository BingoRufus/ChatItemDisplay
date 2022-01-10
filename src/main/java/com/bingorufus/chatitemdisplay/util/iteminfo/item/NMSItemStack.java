package com.bingorufus.chatitemdisplay.util.iteminfo.item;

import net.minecraft.world.item.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import static com.bingorufus.chatitemdisplay.util.iteminfo.reflection.ReflectionClasses.*;

public class NMSItemStack {
    private static Method asNMSCopy;
    private static Method setName;
    private static Method getName;
    private static Method getItem;
    private static Method fromNMS;
    private static Method getTag;
    private static Method setTag;

    static {
        try {
            asNMSCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
            setName = Arrays.stream(nmsItemStack.getDeclaredMethods()).filter(method -> method.getReturnType().equals(nmsItemStack)).filter(method -> method.getParameterCount() == 1).filter(method -> method.getParameterTypes()[0].equals(iChatBaseComponent)).findFirst().orElseThrow(() -> new NoSuchMethodException("Could not obtain the setName method"));
            getName = Arrays.stream(nmsItemWorld.getMethods()).filter(method -> method.getParameterCount() == 1).filter(method -> method.getReturnType().equals(iChatBaseComponent)).filter(method -> method.getParameterTypes()[0].equals(nmsItemStack)).findFirst().orElseThrow(() -> new NoSuchMethodException("Cannot find a method to obtain the item name"));
            getItem = Arrays.stream(nmsItemStack.getMethods()).filter(method -> method.getReturnType().equals(Item.class)).filter(method -> method.getParameterCount() == 0).findFirst().orElseThrow(() -> new NoSuchMethodException("Cannot obtain an nms item object"));
            fromNMS = craftItemStack.getMethod("asBukkitCopy", nmsItemStack);
            getTag = Arrays.stream(nmsItemStack.getMethods()).filter(method -> method.getParameterCount() == 0).filter(method -> method.getReturnType().equals(nbtTagCompound)).max(Comparator.comparing(Method::getName)).orElseThrow(() -> new NoSuchMethodException("Cannot find a method to get ItemStack nbt"));
            setTag = Arrays.stream(nmsItemStack.getMethods()).filter(method -> method.getParameterCount() == 1).filter(method -> method.getParameterTypes()[0].equals(nbtTagCompound)).filter(method -> method.getReturnType().equals(Void.TYPE)).findFirst().orElseThrow(() -> new NoSuchMethodException("Cannot find a method to set ItemStack nbt"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    protected Object item;


    private NMSItemStack(Object nmsItem) {
        this.item = nmsItem;
    }

    public static NMSItemStack fromNMSItem(Object nmsItem) {
        return new NMSItemStack(nmsItem);
    }

    public static NMSItemStack fromBukkitItem(ItemStack itemStack) {
        return new NMSItemStack(toNmsItem(itemStack));
    }

    private static @Nullable Object toNmsItem(ItemStack item) {
        try {
            return asNMSCopy.invoke(craftItemStack, item);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public NMSNBTTag getTag() {
        try {
            return new NMSNBTTag(getTag.invoke(item));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setTag(NMSNBTTag tag) {
        try {
            setTag.invoke(item, tag.getNbtTag());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean hasNBT() {
        return getTag().getNbtTag() == null;
    }

    public NMSChatTag getItemName() {
        try {
            return new NMSChatTag(getName.invoke(getAsItem()));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setItemName(NMSChatTag name) {
        try {
            item = setName.invoke(item, name.getNmsTag());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Object getAsItem() {
        try {
            return getItem.invoke(item);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ItemStack convertToBukkitItemStack() {
        try {
            return (ItemStack) fromNMS.invoke(craftItemStack, item);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getNMSItem() {
        return item;
    }


}
