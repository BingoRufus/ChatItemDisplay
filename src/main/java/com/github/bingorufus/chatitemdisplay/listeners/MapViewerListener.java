package com.github.bingorufus.chatitemdisplay.listeners;

import com.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class MapViewerListener implements Listener {
    final ChatItemDisplay m;

    public MapViewerListener(ChatItemDisplay m) {
        this.m = m;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (m.viewingMap.containsKey(e.getPlayer())) {
            restore(e.getPlayer());

        }
    }

    @EventHandler
    public void onHandSwap(PlayerSwapHandItemsEvent e) {
        if (m.viewingMap.containsKey(e.getPlayer())) {
            e.setCancelled(true);

            restore(e.getPlayer());

        }
    }

    @EventHandler
    public void onItemChange(PlayerItemHeldEvent e) {
        if (m.viewingMap.containsKey(e.getPlayer())) {
            restore(e.getPlayer());

        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (m.viewingMap.containsKey(e.getPlayer())) {
            e.setCancelled(true);
            restore(e.getPlayer());

        }
    }

    @EventHandler
    public void onClickEntity(PlayerInteractAtEntityEvent e) {
        if (m.viewingMap.containsKey(e.getPlayer())) {
            e.setCancelled(true);
            restore(e.getPlayer());

        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        Player p = (Player) e.getEntity();
        if (m.viewingMap.containsKey(p)) {
            restore(p);

        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (m.viewingMap.containsKey(p)) {
            e.setCancelled(true);
            restore(p);
        }
    }

    @EventHandler
    public void onCreativeClick(InventoryCreativeEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (m.viewingMap.containsKey(p)) {
            e.setCancelled(true);
            restore(p);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (m.viewingMap.containsKey(e.getPlayer())) {
            e.getItemDrop().remove();
            restore(e.getPlayer());
        }
    }

    @EventHandler
    public void onHang(HangingPlaceEvent e) {
        if (m.viewingMap.containsKey(e.getPlayer())) {
            e.setCancelled(true);
            restore(e.getPlayer());
        }

    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        Player p = (Player) e.getEntity();
        if (!m.viewingMap.containsKey(p))
            return;
        if (e.getItem().getItemStack().isSimilar(p.getInventory().getItemInMainHand())) {
            e.setCancelled(true);
            restore(p);
        }

    }

    public void restore(Player p) {
        p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        ItemStack item = m.viewingMap.get(p);
        if (item.getItemMeta() != null)
            p.getInventory().setItemInMainHand(item);
        m.viewingMap.remove(p);
    }


}
