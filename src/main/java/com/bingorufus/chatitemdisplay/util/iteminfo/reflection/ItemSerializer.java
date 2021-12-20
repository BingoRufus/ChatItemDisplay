package com.bingorufus.chatitemdisplay.util.iteminfo.reflection;

import com.bingorufus.chatitemdisplay.util.ReflectionClassRetriever;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.bingorufus.chatitemdisplay.util.iteminfo.reflection.ItemStackReflection.getNBT;

public class ItemSerializer {

    private static final Class<?> craftItemStack = ReflectionClassRetriever.getCraftBukkitClassOrThrow("inventory.CraftItemStack");
    private static Class<?> mojangsonParser = ReflectionClassRetriever.getNMSClass("MojangsonParser");
    private static Class<?> nmsItemStack = ReflectionClassRetriever.getNMSClass("ItemStack");

    static {
        if (mojangsonParser == null) {
            mojangsonParser = ReflectionClassRetriever.getNMSClassOrThrow("nbt.MojangsonParser");
        }
        if (nmsItemStack == null) {
            nmsItemStack = ReflectionClassRetriever.getNMSClassOrThrow("world.item.ItemStack");
        }
    }


    public static Object parseNbt(String nbt) {
        try {
            Method parseNBT = mojangsonParser.getMethod("parse", String.class);

            return parseNBT.invoke(mojangsonParser, nbt); // Turns
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Object nmsItem(ItemStack item) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {

        Method asNms = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
        asNms.setAccessible(true);

        return asNms.invoke(craftItemStack, item);

    }

    public static String serialize(ItemStack item) {
        JsonObject itemJson = new JsonObject();
        itemJson.addProperty("id", item.getType().getKey().toString());
        itemJson.addProperty("Count", item.getAmount());
        itemJson.addProperty("tag", getNBT(item));
        return itemJson.toString();
    }

    public static ItemStack deserialize(String json) {
        JsonObject itemJson = (JsonObject) new JsonParser().parse(json);
        Material mat = Material.matchMaterial(itemJson.get("id").getAsString());
        mat = mat == null ? Material.STONE : mat; // If the item type does not exist (If the version changed)

        int count = itemJson.get("Count").getAsInt();

        ItemStack item = new ItemStack(mat, count);
        try {

            Object nmsItem = nmsItem(item);

            Object nbtCompound = parseNbt(itemJson.get("tag").getAsString());

            // NBT
            // string into
            // NBTTagCompound

            Method setTag = nmsItemStack.getMethod("setTag", nbtCompound.getClass());
            setTag.invoke(nmsItem, nbtCompound);

            Method asBukkitCopy = craftItemStack.getMethod("asBukkitCopy", nmsItemStack);
            return (ItemStack) asBukkitCopy.invoke(craftItemStack, nmsItem);

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        return item;

    }



}
