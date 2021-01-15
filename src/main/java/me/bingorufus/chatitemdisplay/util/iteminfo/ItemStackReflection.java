package me.bingorufus.chatitemdisplay.util.iteminfo;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ItemStackReflection {

    private static Class<?> craftPotionUtil = null;
    private static Class<?> craftItemStack = null;
    private static Class<?> chatSerializer = null;
    private static Class<?> iChatBase = null;
    private static Class<?> nbtTagCompound = null;
    private static Class<?> nbtTagList = null;
    private static Class<?> nbtTagString = null;
    private static Class<?> nbtBase = null;

    private static Class<?> nmsItemStack;

    static {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            craftPotionUtil = Class
                    .forName("org.bukkit.craftbukkit.{v}.potion.CraftPotionUtil".replace("{v}", version));

            craftItemStack = Class
                    .forName("org.bukkit.craftbukkit.{v}.inventory.CraftItemStack".replace("{v}", version));
            chatSerializer = Class
                    .forName("net.minecraft.server.{v}.IChatBaseComponent$ChatSerializer".replace("{v}", version));
            iChatBase = Class.forName("net.minecraft.server.{v}.IChatBaseComponent".replace("{v}", version));

            nbtTagCompound = Class.forName("net.minecraft.server.{v}.NBTTagCompound".replace("{v}", version));
            nbtTagList = Class.forName("net.minecraft.server.{v}.NBTTagList".replace("{v}", version));
            nbtTagString = Class.forName("net.minecraft.server.{v}.NBTTagString".replace("{v}", version));
            nbtBase = Class.forName("net.minecraft.server.{v}.NBTBase".replace("{v}", version));
            nmsItemStack = Class.forName("net.minecraft.server.{v}.ItemStack".replace("{v}", version));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private Object nmsItem(ItemStack item) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {

        Method asNms = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
        asNms.setAccessible(true);
        return asNms.invoke(craftItemStack, item);

    }

    private Object toChatComponent(BaseComponent... component) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method toString = chatSerializer.getDeclaredMethod("a", String.class);
        return toString.invoke(chatSerializer, ComponentSerializer.toString(component));
    }

    public BaseComponent getOldHover(ItemStack item) {
        try {
            Object nmsItem = nmsItem(item);
            Method getChatComponent = nmsItem.getClass().getMethod("B");
            Object chatComponent = getChatComponent.invoke(nmsItem);

            Method serialze = chatSerializer.getMethod("a", iChatBase);
            String s = (String) serialze.invoke(chatSerializer, iChatBase.cast(chatComponent));
            return ComponentSerializer.parse(s)[0];
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException ignored) {

        }

        return new TextComponent();
    }

    public boolean hasNbt(ItemStack item) {
        try {
            Object nmsItem = nmsItem(item);
            if (nmsItem == null) {
                throw new IllegalArgumentException(
                        item.getType().name() + " could not be turned into a net.minecraft item");
            }
            Method hasTag = nmsItem.getClass().getMethod("hasTag");


            return (boolean) hasTag.invoke(nmsItem);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            return false;
        }

    }

    public String getNBT(ItemStack item) {
        try {
            Object nmsItem = nmsItem(item);
            if (nmsItem == null) {
                throw new IllegalArgumentException(
                        item.getType().name() + " could not be turned into a net.minecraft item");
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

    private ItemStack fromNMS(Object nmsItem) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method fromNMS = craftItemStack.getMethod("asBukkitCopy", nmsItemStack);
        fromNMS.setAccessible(true);
        return (ItemStack) fromNMS.invoke(craftItemStack, nmsItem);
    }

    public ItemStack setItemName(final ItemStack item, final BaseComponent name) {
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


    public ItemStack setLore(final ItemStack item, final BaseComponent... lore) {
        try {
            Object nms = nmsItem(item);
            if (!nms.getClass().equals(nmsItemStack)) return item;
            Method setTag = nmsItemStack.getDeclaredMethod("setTag", nbtTagCompound);

            if (!hasNbt(item)) {
                setTag.invoke(nms, nbtTagCompound.newInstance());
            }
            Method getTag = nmsItemStack.getMethod("getTag");
            Object mainTag = getTag.invoke(nms);
            Method nbtSet = nbtTagCompound.getDeclaredMethod("set", String.class, nbtBase);
            Method nbtGetSubTag = nbtTagCompound.getDeclaredMethod("getCompound", String.class);
            if (nbtGetSubTag.invoke(mainTag, "display") == null) {
                nbtSet.invoke(mainTag, "display", nbtTagCompound.newInstance());

            }
            Object displayTag = nbtGetSubTag.invoke(mainTag, "display");
            Object loreList = nbtTagList.newInstance();
            Optional<Method> createStringOptional = Arrays.stream(nbtTagString.getDeclaredMethods()).filter(method -> method.getReturnType().equals(nbtTagString)).filter(method -> method.getParameterCount() == 1).filter(method -> method.getParameterTypes()[0].equals(String.class)).findFirst();
            if (!createStringOptional.isPresent()) return item;
            Method createNBTString = createStringOptional.get();
            for (BaseComponent component : lore) {
                List<Object> list = (List<Object>) loreList;
                String json = ComponentSerializer.toString(component);
                Object nbtString = createNBTString.invoke(createNBTString, json);
                list.add(nbtString);
            }
            nbtSet.invoke(displayTag, "Lore", loreList);
            nbtSet.invoke(mainTag, "display", displayTag);
            setTag.invoke(nms, mainTag);
            return fromNMS(nms);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }
        return item;
    }
}
