package com.bingorufus.chatitemdisplay.util.iteminfo.reflection;

import com.bingorufus.chatitemdisplay.util.ReflectionClassRetriever;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.world.item.Item;
import org.bukkit.Warning;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class Post17ItemStackReflection implements ReflectionInterface {
    private static final Class<?> craftItemStack = ReflectionClassRetriever.getCraftBukkitClassOrThrow("inventory.CraftItemStack");
    private static final Class<?> nmsItemStack = ReflectionClassRetriever.getNMSClassOrThrow("world.item.ItemStack");
    private static final Class<?> iChatBase = ReflectionClassRetriever.getNMSClassOrThrow("network.chat.IChatBaseComponent");

    @Override
    public BaseComponent getOldHover(ItemStack item) {
        return new TextComponent();
    }

    @Override
    @Warning(reason = "This has not been updated ")
    public boolean hasNbt(ItemStack item) {
        try {
            net.minecraft.world.item.ItemStack nmsItem = (net.minecraft.world.item.ItemStack) nmsItem(item);
            if (nmsItem == null) {
                throw new IllegalArgumentException(item.getType().name() + " could not be turned into a net.minecraft item");
            }

            return nmsItem.r();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            return false;

        }
    }

    @Override
    public String getNBT(ItemStack item) {
        try {
            net.minecraft.world.item.ItemStack nmsItem = (net.minecraft.world.item.ItemStack) nmsItem(item);

            NBTTagCompound tag = nmsItem.t();


            return tag.toString();

        } catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    @Override
    public String translateItemStack(ItemStack holding) {
        return translateItemStackComponent(holding).toString();

    }

    @Override
    public ItemStack setItemName(ItemStack item, BaseComponent name) {
        try {
            Object nms = nmsItem(item);
            Optional<Method> setNameOptional = Arrays.stream(nms.getClass().getDeclaredMethods()).filter(method -> method.getReturnType().equals(nms.getClass())).filter(method -> method.getParameterCount() == 1).filter(method -> method.getParameterTypes()[0].equals(iChatBase)).findFirst();
            if (!setNameOptional.isPresent()) return item;
            Method setName = setNameOptional.get();
            setName.invoke(nms, toChatComponent(name));
            return fromNMS(nms);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public ItemStack setLore(ItemStack item, BaseComponent... lore) {
        try {

            net.minecraft.world.item.ItemStack nmsItem = (net.minecraft.world.item.ItemStack) nmsItem(item);

            NBTTagCompound tag = nmsItem.t();
            if (!tag.b("display")) tag.a("display", new NBTTagCompound());
            NBTTagCompound displayTag = (NBTTagCompound) tag.c("display");
            NBTTagList loreList = new NBTTagList();
            for (BaseComponent loreLine : lore) {
                loreList.add(NBTTagString.a(ComponentSerializer.toString(loreLine)));
            }
            if (displayTag == null) {
                throw new NullPointerException("No available display tag");
            }
            displayTag.a("Lore", loreList);
            tag.a("display", displayTag);
            nmsItem.c(tag);
            return fromNMS(nmsItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    @Override
    public TextComponent translateItemStackComponent(ItemStack holding) {
        try {
            net.minecraft.world.item.ItemStack nmsItem = (net.minecraft.world.item.ItemStack) nmsItem(holding);

            Method getItem = Arrays.stream(nmsItem.getClass().getMethods()).filter(method -> method.getReturnType().equals(Item.class)).filter(method -> method.getParameterCount() == 0).findFirst().orElseThrow(() -> new NoSuchMethodException("Cannot obtain an nms item object"));
            Item newItem = (Item) getItem.invoke(nmsItem);
            Method getName = Arrays.stream(newItem.getClass().getMethods()).filter(method -> method.getParameterCount() == 1).filter(method -> method.getReturnType().equals(IChatBaseComponent.class)).filter(method -> method.getParameterTypes()[0].equals(net.minecraft.world.item.ItemStack.class)).findFirst().orElseThrow(() -> new NoSuchMethodException("Cannot find a method to obtain the item name"));
            IChatBaseComponent chatComp = (IChatBaseComponent) getName.invoke(newItem, nmsItem);

            BaseComponent[] bc = ComponentSerializer.parse(IChatBaseComponent.ChatSerializer.a(chatComp));
            return new TextComponent(bc);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        return new TextComponent();
    }

    private Object nmsItem(ItemStack item) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {

        Method asNms = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
        asNms.setAccessible(true);
        return asNms.invoke(craftItemStack, item);

    }

    private ItemStack fromNMS(Object nmsItem) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method fromNMS = craftItemStack.getMethod("asBukkitCopy", nmsItemStack);
        fromNMS.setAccessible(true);
        return (ItemStack) fromNMS.invoke(craftItemStack, nmsItem);
    }

    private IChatMutableComponent toChatComponent(BaseComponent... component) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(component));

    }

}
