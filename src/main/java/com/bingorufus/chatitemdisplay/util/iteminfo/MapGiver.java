package com.bingorufus.chatitemdisplay.util.iteminfo;

import com.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class MapGiver {

    public static void giveMap(Player player, ItemStack map) {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        PacketContainer itemPacket = pm.createPacket(PacketType.Play.Server.WINDOW_ITEMS);
        itemPacket.getIntegers().write(0, 0);
        List<ItemStack> newInventory = cloneToProtocolList(player.getInventory());
        newInventory.set(player.getInventory().getHeldItemSlot() + 36, map);
        itemPacket.getItemListModifier().write(0, newInventory);
        player.closeInventory();
        Bukkit.getScheduler().scheduleSyncDelayedTask(ChatItemDisplay.getInstance(), () -> {

            try {
                pm.sendServerPacket(player, itemPacket);
            } catch (InvocationTargetException e) {
                InvocationTargetException invocationTargetException = new InvocationTargetException(e, "Cannot send packet to " + player.getName());
                invocationTargetException.printStackTrace();
            }
        }, 2);


    }

    private static List<ItemStack> cloneToProtocolList(PlayerInventory inventory) {
        List<ItemStack> out = new ArrayList<>();
        IntStream.rangeClosed(0, 4).boxed().forEach(i -> out.add(null)); //Sets the crafting table contents to null
        List<ItemStack> armor = Arrays.asList(inventory.getArmorContents());
        Collections.reverse(armor);
        out.addAll(armor);  //Gets the armor, flips the order, then adds it to the list
        IntStream.rangeClosed(9, 35).boxed().map(inventory::getItem).forEach(out::add); //adds the inventory items NOT in the hotbar
        IntStream.rangeClosed(0, 8).boxed().map(inventory::getItem).forEach(out::add); //adds the hotbar
        out.add(inventory.getItemInOffHand()); //Adds the offhand
        out.replaceAll(itemStack -> itemStack == null ? new ItemStack(Material.AIR) : itemStack);
        return out;
    }
}
