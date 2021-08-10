package io.github.bingorufus.chatitemdisplay.util.iteminfo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.bingorufus.chatitemdisplay.util.ReflectionClassRetriever;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ItemSerializer {

    private static final Class<?> craftItemStack = ReflectionClassRetriever.getCraftBukkitClassOrThrow("inventory.CraftItemStack");
    private static final Class<?> mojangsonParser = ReflectionClassRetriever.getNMSClassOrThrow("MojangsonParser");
    private static final Class<?> nmsItemStack = ReflectionClassRetriever.getNMSClassOrThrow("ItemStack");


    private ItemSerializer() {

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

    private static String getNBT(ItemStack item) {

        try {
            Object nmsItem = nmsItem(item);
            if (nmsItem == null) {
                throw new IllegalArgumentException(item.getType().name() + " could not be converted to NMS");
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

}
