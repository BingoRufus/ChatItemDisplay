package com.bingorufus.chatitemdisplay.util.iteminfo;

import com.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.displayables.DisplayInventoryType;
import com.bingorufus.chatitemdisplay.util.iteminfo.reflection.ItemStackReflection;
import com.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public class PlayerInventoryReplicator {
    final ChatItemDisplay m;

    public PlayerInventoryReplicator() {
        m = ChatItemDisplay.getInstance();
    }

    public InventoryData replicateInventory(Player p) {
        String invTitle = StringFormatter
                .format(ChatItemDisplayAPI.getRegisteredDisplayables().stream().filter(DisplayInventoryType.class::isInstance).findFirst().get().getInventoryTitle().replaceAll("%player%",
                        m.getConfig().getBoolean("use-nicks-in-gui") ? m.getConfig().getBoolean("strip-nick-colors-gui")
                                ? ChatColor.stripColor(p.getDisplayName())
                                : p.getDisplayName() : p.getName()));
        Inventory inv = Bukkit.createInventory(null, 45, invTitle);


        PlayerInventory i = p.getInventory();
        inv.setItem(0, i.getHelmet());
        inv.setItem(1, i.getChestplate());
        inv.setItem(2, i.getLeggings());
        inv.setItem(3, i.getBoots());
        inv.setItem(8, i.getItemInOffHand());
        ItemStack[] contents = i.getStorageContents();
        for (int num = 0; num < contents.length; num++) {
            inv.setItem(num < 9 ? num + 36 : num, contents[num]);
        }


        ItemStack playerStats = new ItemStack(Material.COOKED_BEEF);
        BaseComponent statName = new TextComponent("§c§b" + p.getDisplayName());
        statName.setItalic(false);
        BaseComponent health = new TextComponent(new TranslatableComponent("stat.minecraft.damage_taken"), new TextComponent(": " + Math.round(p.getHealth()) + " / " + p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        health.setColor(ChatColor.RED);
        health.setItalic(false);
        BaseComponent food = new TextComponent(new TranslatableComponent("effect.minecraft.hunger"), new TextComponent(": " + Math.round(p.getFoodLevel()) + " / 20"));
        food.setColor(ChatColor.GOLD);
        food.setItalic(false);
        TranslatableComponent xp = new TranslatableComponent("container.enchant.level.many");
        xp.addWith(p.getLevel() + "");
        xp.setColor(ChatColor.GREEN);
        xp.setItalic(false);
        playerStats = ItemStackReflection.setLore(playerStats, health, food, xp);
        playerStats = ItemStackReflection.setItemName(playerStats, statName);
        inv.setItem(5, playerStats);


        ItemStack potionEffects = new ItemStack(Material.POTION);
        PotionMeta pm = (PotionMeta) potionEffects.getItemMeta();
        pm.setBasePotionData(new PotionData(PotionType.WATER));
        for (PotionEffect potionEffect : p.getActivePotionEffects()) {
            pm.addCustomEffect(potionEffect, true);
        }
        potionEffects.setItemMeta(pm);
        BaseComponent potionName = new TranslatableComponent("item.minecraft.potion");
        potionName.setBold(true);
        potionName.setItalic(false);
        potionName.setColor(ChatColor.BLUE);

        if (p.getFireTicks() > 0) {
            BaseComponent burning = new TranslatableComponent("subtitles.entity.generic.burn");
            burning.setBold(false);
            burning.setItalic(false);
            burning.setColor(ChatColor.GOLD);
            potionEffects = ItemStackReflection.setLore(potionEffects, burning);
        }
        potionEffects = ItemStackReflection.setItemName(potionEffects, potionName);
        inv.setItem(6, potionEffects);


        return new InventoryData(inv, invTitle);
    }


}
