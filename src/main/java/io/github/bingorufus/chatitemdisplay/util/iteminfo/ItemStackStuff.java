package io.github.bingorufus.chatitemdisplay.util.iteminfo;

import com.google.gson.JsonObject;
import io.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import io.github.bingorufus.chatitemdisplay.util.iteminfo.reflection.ItemStackReflection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class ItemStackStuff {

    public static BaseComponent getName(ItemStack item, String color, boolean forceColor) {
        ItemRarity rarity = ItemRarity.getRarity(item);
        String colorPrefix = color + rarity.getColor();
        BaseComponent legacy = TextComponent.fromLegacyText(forceColor ? color : colorPrefix)[0];
        if (Objects.requireNonNull(item.getItemMeta()).hasDisplayName()) {
            colorPrefix += ChatColor.ITALIC;
            TextComponent itemName = new TextComponent(TextComponent.fromLegacyText(colorPrefix + item.getItemMeta().getDisplayName()));
            if (forceColor) {
                itemName = (TextComponent) TextComponent.fromLegacyText(color + ChatColor.stripColor(item.getItemMeta().getDisplayName()))[0];
                itemName.copyFormatting(legacy, FormatRetention.FORMATTING, true);

            }
            return itemName;
        }

        if (item.getItemMeta() instanceof SkullMeta) {
            SkullMeta sm = (SkullMeta) item.getItemMeta();
            if (sm.hasOwner() && sm.getOwningPlayer() != null && sm.getOwningPlayer().getName() != null) {
                TranslatableComponent translatable = new TranslatableComponent("block.minecraft.player_head.named");
                translatable.addWith(new TextComponent(sm.getOwningPlayer().getName()));
                translatable.setColor(TextComponent.fromLegacyText(colorPrefix)[0].getColor());
                if (forceColor) {
                    translatable.copyFormatting(legacy, FormatRetention.FORMATTING, true);

                }
                return translatable;
            }
        }

        if (item.getItemMeta() instanceof BookMeta) {
            BookMeta bm = (BookMeta) item.getItemMeta();
            if (bm.hasTitle()) {
                TextComponent book = new TextComponent(colorPrefix + bm.getTitle());
                book.copyFormatting(legacy, FormatRetention.FORMATTING, false);

                if (forceColor) {
                    book.copyFormatting(legacy, FormatRetention.FORMATTING, true);
                }
                return book;
            }
        }

        TranslatableComponent tr = new TranslatableComponent(ItemStackReflection.translateItemStack(item));
        tr.copyFormatting(legacy, FormatRetention.FORMATTING, false);

        if (forceColor) {
            tr.copyFormatting(legacy, FormatRetention.FORMATTING, true);
        }
        return tr;

    }

    public static String makeStringPretty(String s) {

        StringBuilder out = new StringBuilder();
        String[] nameParts = s.toLowerCase().split("_");
        for (String part : nameParts) {
            part = part.substring(0, 1).toUpperCase() + part.substring(1);
            if (out.toString().equals("")) {
                out = new StringBuilder(part);
                continue;
            }

            out.append(" ");
            out.append(part);
        }

        return out.toString();

    }

    private static String getItemKey(ItemStack item) {
        String itemKey = item.getType().getKey().getKey().toLowerCase();
        if (item.getType().name().equalsIgnoreCase("CROSSBOW")) {
            CrossbowMeta cm = (CrossbowMeta) item.getItemMeta();
            if (cm == null) return itemKey;
            if (!cm.hasChargedProjectiles())
                return itemKey + "_standby";

            return itemKey + "_" + cm.getChargedProjectiles().get(0).getType().getKey().getKey().toLowerCase()
                    .replace("firework_rocket", "firework");
        }
        return itemKey;

    }

    public static BufferedImage getImage(ItemStack item, int... size) {
        return null;


    }

    private static BufferedImage resizeImage(BufferedImage bi, int[] size) {
        BufferedImage resizeImage = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = resizeImage.createGraphics();
        g2D.drawImage(bi, 0, 0, size[0], size[1], null);
        g2D.dispose();
        return resizeImage;
    }

    public static String getLangName(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) return item.getItemMeta().getDisplayName();
        JsonObject jo = ChatItemDisplay.getInstance().getLang();
        String translationKey = ItemStackReflection.translateItemStack(item);
        return jo.has(translationKey) ? jo.get(translationKey).getAsString() : itemName(item);
    }

    public static String itemName(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return "";

        String out = "";
        ItemRarity r = ItemRarity.getRarity(item);
        out += r.getColor();
        if (Objects.requireNonNull(item.getItemMeta()).hasDisplayName()) {
            out += item.getItemMeta().getDisplayName();
            return out;

        }
        return out + makeStringPretty(item.getType().name());

    }

}
