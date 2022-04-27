package com.bingorufus.chatitemdisplay.util.iteminfo.item;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.ComponentConverter;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.comphenix.protocol.wrappers.nbt.NbtWrapper;
import lombok.SneakyThrows;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import static com.bingorufus.chatitemdisplay.util.iteminfo.reflection.ReflectionClasses.*;

public class NMSItemStack {
    private static Method setName;
    private static Method getName;
    private static Method getItem;

    static {
        try {
            setName = Arrays.stream(nmsItemStack.getDeclaredMethods()).filter(method -> method.getReturnType().equals(nmsItemStack)).filter(method -> method.getParameterCount() == 1).filter(method -> method.getParameterTypes()[0].equals(iChatBaseComponent)).findFirst().orElseThrow(() -> new NoSuchMethodException("Could not obtain the setName method"));
            getName = Arrays.stream(nmsItemWorld.getMethods()).filter(method -> method.getParameterCount() == 1).filter(method -> method.getReturnType().equals(iChatBaseComponent)).filter(method -> method.getParameterTypes()[0].equals(nmsItemStack)).findFirst().orElseThrow(() -> new NoSuchMethodException("Cannot find a method to obtain the item name"));
            getItem = Arrays.stream(nmsItemStack.getMethods()).filter(method -> method.getReturnType().equals(MinecraftReflection.getItemClass())).filter(method -> method.getParameterCount() == 0).findFirst().orElseThrow(() -> new NoSuchMethodException("Cannot obtain an nms item object"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    protected ItemStack item;


    public NMSItemStack(ItemStack bukkitItem) {
        this.item = bukkitItem;
    }

    public static NMSItemStack fromNMSItem(Object nmsItem) {
        return new NMSItemStack(MinecraftReflection.getBukkitItemStack(nmsItem));
    }

    public static NMSItemStack fromBukkitItem(ItemStack itemStack) {
        return new NMSItemStack(itemStack);
    }


    @SneakyThrows(InvocationTargetException.class)
    public NbtCompound getTag() {
        NbtWrapper<?> nbt = NbtFactory.fromItemTag(item);
        if (nbt instanceof NbtCompound) {
            return (NbtCompound) nbt;
        } else {
            throw new InvocationTargetException(new Throwable(), String.format("Could not get the tag as an nbtCompound, received %s instead", nbt.getClass().getSimpleName()));
        }

    }

    public void setTag(NbtCompound tag) {
        NbtFactory.setItemTag(item, tag);
    }

    public boolean hasNBT() {
        return getTag().getHandle() == null;
    }

    public NMSChatTag getItemName() {
        try {
            return new NMSChatTag(getName.invoke(getAsItem()));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setItemName(WrappedChatComponent name) {
        Object nmsItem = MinecraftReflection.getMinecraftItemStack(item);
        try {
            nmsItem = setName.invoke(nmsItem, name.getHandle());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        item = MinecraftReflection.getBukkitItemStack(nmsItem);
    }

    public Object getAsItem() {
        try {
            return getItem.invoke(MinecraftReflection.getMinecraftItemStack(item));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ItemStack getBukkitItem() {
        return item;
    }

    public void setLore(NbtList<?> lore) {
        NbtCompound tag = getTag();
        NbtCompound displayTag = tag.getCompound("display");
        displayTag.put("Lore", lore);
        //TODO: Remove
        System.out.println(displayTag.getType());
        tag.put("display", displayTag);
        setTag(tag);
    }

    public void setLore(BaseComponent... components) {
        NbtList<?> list = NbtFactory.ofList("Lore");
        //Name: "\"text\"
        // Value: whatever the text says
        WrappedChatComponent wc = ComponentConverter.fromBaseComponent(components);
        NbtCompound c = NbtFactory.fromNMSCompound(wc.getHandle());
        Object[] chatComponents = new Object[components.length];
        for (int i = 0; i < components.length; i++) {
            chatComponents[i] = ComponentConverter.fromBaseComponent(components[i]).getHandle();
        }
        setLore(NbtFactory.ofList("Lore", chatComponents));

    }


}
