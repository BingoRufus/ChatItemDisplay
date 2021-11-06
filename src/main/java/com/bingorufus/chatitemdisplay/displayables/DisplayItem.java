package com.bingorufus.chatitemdisplay.displayables;

import com.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.api.display.DisplayType;
import com.bingorufus.chatitemdisplay.api.display.Displayable;
import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.bingorufus.chatitemdisplay.util.iteminfo.ItemStackStuff;
import com.bingorufus.chatitemdisplay.util.iteminfo.reflection.ItemSerializer;
import com.bingorufus.chatitemdisplay.util.iteminfo.reflection.ItemStackReflection;
import com.bingorufus.chatitemdisplay.util.string.StringFormatter;
import com.bingorufus.chatitemdisplay.util.string.VersionComparator;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.Bukkit;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;
import java.util.regex.Matcher;

public class DisplayItem extends Displayable {
    private ItemStack item;

    public DisplayItem(Player displayer) {
        super(displayer);
        item = displayer.getInventory().getItemInMainHand().clone();
    }

    public DisplayItem(JsonObject data) {
        super(data);
    }

    @Override
    protected Class<? extends DisplayType<?>> getTypeClass() {
        return DisplayItemType.class;
    }

    @Override
    public BaseComponent getDisplayComponent() {
        String format = (item.getAmount() > 1
                ? ChatItemConfig.CHAT_ITEM_FORMAT_MULTIPLE.getCachedValue()
                : ChatItemConfig.CHAT_ITEM_FORMAT.getCachedValue());

        return format(format);
    }

    @Override
    public Inventory onViewDisplay(Player viewer) {
        String guiName = StringFormatter.format(getType().getInventoryTitle()).replace("%name%", Matcher.quoteReplacement(ItemStackStuff.getLangName(item)));
        Inventory inventory = Bukkit.createInventory(null, 9,
                guiName.replaceAll("%player%",
                        ChatItemConfig.getConfig().getBoolean("use-nicks-in-gui") ? ChatItemConfig.getConfig().getBoolean("strip-nick-colors-gui")
                                ? ChatColor.stripColor(getDisplayer().getDisplayName())
                                : getDisplayer().getDisplayName() : getDisplayer().getRegularName()));
        inventory.setItem(4, item);

        return inventory;
    }


    @Override
    public String getLoggerMessage() {

        String format = (item.getAmount() > 1
                ? ChatItemConfig.CHAT_ITEM_FORMAT_MULTIPLE.getCachedValue()
                : ChatItemConfig.CHAT_ITEM_FORMAT.getCachedValue());
        if (format == null) return "";
        format = format.replaceAll("%amount%", item.getAmount() + "");
        format = format.replaceAll("%item%", Matcher.quoteReplacement(ItemStackStuff.getLangName(item)));

        return ChatColor.stripColor(StringFormatter.format(format));
    }

    @Override
    public JsonObject serializeData() {
        JsonObject jo = new JsonObject();
        jo.addProperty("item", ItemSerializer.serialize(item));
        return jo;
    }

    @Override
    public void deseralizeData(JsonObject data) {
        String nbt = data.get("item").getAsString();
        item = ItemSerializer.deserialize(nbt);
    }

    @Override
    public void broadcastDisplayable() {
        String format = StringFormatter.format(item.getAmount() > 1
                ? ChatItemConfig.COMMAND_ITEM_FORMAT_MULTIPLE.getCachedValue()
                : ChatItemConfig.COMMAND_ITEM_FORMAT.getCachedValue());
        Bukkit.spigot().broadcast(format(format));
    }

    @Override
    public boolean hasBlacklistedItem() {
        if (isBlacklisted(item)) return true;

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof BlockStateMeta) {
            BlockStateMeta bsm = (BlockStateMeta) meta;
            if (bsm.getBlockState() instanceof Container) {
                Container c = (Container) bsm.getBlockState();
                return containsBlacklistedItem(c.getInventory());
            }
        }
        return false;
    }

    private TextComponent format(String s) {
        s = s.replaceAll("%player%",
                ChatItemConfig.getConfig().getBoolean("use-nicks-in-display-message")
                        ? ChatItemConfig.getConfig().getBoolean("strip-nick-colors-message")
                        ? ChatColor.stripColor(getDisplayer().getDisplayName())
                        : getDisplayer().getDisplayName()
                        : getDisplayer().getRegularName());

        s = StringFormatter.format(s);

        String[] parts = s.split("((?<=%item%)|(?=%item%)|(?<=%amount%)|(?=%amount%))");
        TextComponent whole = new TextComponent();
        TextComponent base = baseHover();
        BaseComponent prev = null;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i > 0) prev = TextComponent.fromLegacyText(whole.getExtra().get(i - 1).toLegacyText())[0];

            if (part.contains("%item%")) {
                whole.addExtra(base);
                continue;
            }
            if (part.contains("%amount%")) {
                TextComponent tc = new TextComponent(item.getAmount() + "");
                if (i > 0)
                    tc.copyFormatting(prev, ComponentBuilder.FormatRetention.FORMATTING, false);
                whole.addExtra(tc);
                continue;
            }

            TextComponent tc = new TextComponent(TextComponent.fromLegacyText(part));
            if (i > 0 && !part.startsWith("§r"))
                tc.copyFormatting(prev, ComponentBuilder.FormatRetention.FORMATTING, false);
            whole.addExtra(tc);
        }
        whole.setHoverEvent(base.getHoverEvent());
        whole.setClickEvent(base.getClickEvent());
        return whole;
    }

    public TextComponent baseHover() {
        String color = StringFormatter.format(ChatItemConfig.getConfig().getString("messages.item-color"));
        BaseComponent bc = ItemStackStuff.getName(item, color,
                ChatItemConfig.getConfig().getBoolean("messages.force-item-colors"));


        TextComponent hover = new TextComponent(bc);


        VersionComparator.Status s = VersionComparator.isRecent(ChatItemDisplay.MINECRAFT_VERSION,
                "1.16");

        if (s.equals(VersionComparator.Status.BEHIND)) {
            hover.setHoverEvent(ItemStackReflection.getOldHover(item).getHoverEvent());

        } else {

            hover.setHoverEvent(
                    new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(item.getType().getKey().toString(),
                            item.getAmount(), ItemTag.ofNbt(ItemStackReflection.getNBT(item)))));
        }
        UUID id = ChatItemDisplayAPI.getDisplayedManager().getDisplay(this).getId();
        hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                "/viewitem " + (id)));

        return hover;
    }
}
