package com.bingorufus.chatitemdisplay.util.iteminfo.reflection;

import com.bingorufus.chatitemdisplay.util.ReflectionClassRetriever;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@SuppressWarnings({})
public class Release17ItemStackReflection implements ReflectionInterface {
    private static final Class<?> craftPotionUtil = ReflectionClassRetriever.getCraftBukkitClassOrThrow("potion.CraftPotionUtil");
    private static final Class<?> craftItemStack = ReflectionClassRetriever.getCraftBukkitClassOrThrow("inventory.CraftItemStack");
    private static final Class<?> nmsItemStack = ReflectionClassRetriever.getNMSClassOrThrow("world.item.ItemStack");
    private static final Class<?> iChatBase = ReflectionClassRetriever.getNMSClassOrThrow("network.chat.IChatBaseComponent");

    @Override
    public BaseComponent getOldHover(ItemStack item) {
        return new TextComponent();
    }

    @Override
    public boolean hasNbt(ItemStack item) {
        try {
            Object nmsItem = nmsItem(item);
            if (nmsItem == null) {
                throw new IllegalArgumentException(item.getType().name() + " could not be turned into a net.minecraft item");
            }
            Method hasTag = nmsItem.getClass().getMethod("hasTag");

            return (boolean) hasTag.invoke(nmsItem);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            return false;

        }
    }

    @Override
    public String getNBT(ItemStack item) {
        try {
            Object nmsItem = nmsItem(item);
            if (nmsItem == null) {
                throw new IllegalArgumentException(item.getType().name() + " could not be turned into a net.minecraft item");
            }
            Method hasTag = nmsItem.getClass().getMethod("hasTag");
            if ((boolean) hasTag.invoke(nmsItem)) {
                Method getTag = nmsItem.getClass().getMethod("getTag");
                Object nbtData = getTag.invoke(nmsItem);
                Method asString = nbtData.getClass().getMethod("asString");
                return (String) asString.invoke(nbtData);
            }

        } catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    @Override
    public String translateItemStack(ItemStack holding) {
        try {
            Object item = nmsItem(holding);
            if (item == null) {
                throw new IllegalArgumentException(holding.getType().name() + " could not be queried!");
            }
            Method getItem = item.getClass().getMethod("getItem");
            getItem.setAccessible(true);
            Object newItem = getItem.invoke(item);
            Method getName = newItem.getClass().getMethod("getName");
            String key = (String) getName.invoke(newItem);
            if (holding.getItemMeta() instanceof PotionMeta) {
                PotionMeta pm = (PotionMeta) holding.getItemMeta();

                key += ".effect.";
                Method fromBukkit = craftPotionUtil.getMethod("fromBukkit", PotionData.class);
                fromBukkit.setAccessible(true);
                String potionkey = (String) fromBukkit.invoke(craftPotionUtil,
                        new PotionData(pm.getBasePotionData().getType()));
                potionkey = potionkey.replaceAll("minecraft:", "");
                key += potionkey;

            }

            return key;

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        return "";

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
            Method getTag = nmsItem.getClass().getDeclaredMethod("getOrCreateTag");
            NBTTagCompound tag = (NBTTagCompound) getTag.invoke(nmsItem);
            Method hasKey = tag.getClass().getMethod("hasKey", String.class);
            Method setTag = tag.getClass().getMethod("set", String.class, NBTBase.class);

            if (!((boolean) hasKey.invoke(tag, "display"))) { // Create display tag if it does not exist
                setTag.invoke(tag, "display", new NBTTagCompound());
            }

            Method getCompound = tag.getClass().getMethod("getCompound", String.class);
            NBTTagCompound displayTag = (NBTTagCompound) getCompound.invoke(tag, "display");

            NBTTagList loreList = new NBTTagList();
            for (BaseComponent loreLine : lore) {
                loreList.add(NBTTagString.a(ComponentSerializer.toString(loreLine)));
            }
            setTag.invoke(displayTag, "Lore", loreList);
            setTag.invoke(tag, "display", displayTag);


            Method itemSetTag = nmsItem.getClass().getMethod("setTag", NBTTagCompound.class);
            itemSetTag.invoke(nmsItem, tag);

            return fromNMS(nmsItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    @Override
    public TextComponent translateItemStackComponent(ItemStack holding) {
        return new TextComponent(new TranslatableComponent(translateItemStack(holding)));
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
