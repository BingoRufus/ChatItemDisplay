package com.bingorufus.chatitemdisplay.util.iteminfo.reflection;

import com.bingorufus.chatitemdisplay.util.iteminfo.item.NMSChatTag;
import com.bingorufus.chatitemdisplay.util.iteminfo.item.NMSItemStack;
import com.comphenix.protocol.wrappers.nbt.NbtType;
import com.comphenix.protocol.wrappers.nbt.NbtWrapper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.bingorufus.chatitemdisplay.util.iteminfo.reflection.ReflectionClasses.*;

public class Pre17ItemStackReflection implements ReflectionInterface {



    private Object toChatComponent(BaseComponent... component) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method toString = chatSerializer.getDeclaredMethod("a", String.class);
        return toString.invoke(chatSerializer, ComponentSerializer.toString(component));
    }

    public BaseComponent getOldHover(ItemStack item) {
        try {
            NMSItemStack nmsItemStack = NMSItemStack.fromBukkitItem(item);
            net.minecraft.world.item.ItemStack a;

            Method getChatComponent = Arrays.stream(nmsItemStack.getClass().getMethods()).filter(method -> method.getReturnType().equals(iChatBaseComponent)).filter(method -> method.getParameterCount() == 0).filter(method -> !method.getName().equals("getName")).findFirst().orElseThrow(() -> new NoSuchMethodException("Cannot find method to convert item to basecomponent"));
            Object chatComponent = getChatComponent.invoke(nmsItemStack);

            Method serialze = Arrays.stream(chatSerializer.getMethods()).filter(method -> method.getParameterCount() == 1).filter(method -> method.getReturnType().equals(String.class)).filter(method -> method.getParameterTypes()[0].equals(iChatBaseComponent)).findFirst().orElseThrow(() -> new NoSuchMethodException("Cannot find method to serialize basecomponent"));
            String s = (String) serialze.invoke(chatSerializer, iChatBaseComponent.cast(chatComponent));
            return ComponentSerializer.parse(s)[0];
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException ignored) {

        }

        return new TextComponent();
    }


    public boolean hasNbt(ItemStack item) {
        NMSItemStack nmsItem = NMSItemStack.fromBukkitItem(item);
        return nmsItem.hasNBT();

    }

    public String getNBT(ItemStack item) {
        NMSItemStack nmsItem = NMSItemStack.fromBukkitItem(item);
        return nmsItem.getTag().toString();


    }


    public String translateItemStack(ItemStack holding) {
        NMSItemStack nmsItem = NMSItemStack.fromBukkitItem(holding);
        return new TextComponent(nmsItem.getItemName().toBaseComponent()).toString();

    }

    private ItemStack fromNMS(Object nmsItem) {
        NMSItemStack nmsItemStack = NMSItemStack.fromNMSItem(nmsItem);
        return nmsItemStack.convertToBukkitItemStack();
    }

    public ItemStack setItemName(final ItemStack item, final BaseComponent name) {
        NMSItemStack nmsItem = NMSItemStack.fromBukkitItem(item);
        nmsItem.setItemName(new NMSChatTag(name));
        return nmsItem.convertToBukkitItemStack();
    }


    public ItemStack setLore(final ItemStack item, final BaseComponent... lore) {
        try {
            NMSItemStack nmsItem = NMSItemStack.fromBukkitItem(item);
            NbtWrapper<?> nbtTag = nmsItem.getTag();
            if (nbtTag.getType() != NbtType.TAG_COMPOUND)
                nbtTag.getType().equals(NbtType.TAG_COMPOUND)

            Method nbtSet = nbtTagCompound.getDeclaredMethod("set", String.class, nbtBase);
            Method nbtGetSubTag = nbtTagCompound.getDeclaredMethod("getCompound", String.class);
            if (nbtGetSubTag.invoke(mainTag, "display") == null) {
                nbtSet.invoke(mainTag, "display", nbtTagCompound.getConstructor().newInstance());
            }
            Object displayTag = nbtGetSubTag.invoke(mainTag, "display");
            Object loreList = nbtTagList.getDeclaredConstructor().newInstance();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public TextComponent translateItemStackComponent(ItemStack holding) {
        return new TextComponent(new TranslatableComponent(translateItemStack(holding)));
    }
}
