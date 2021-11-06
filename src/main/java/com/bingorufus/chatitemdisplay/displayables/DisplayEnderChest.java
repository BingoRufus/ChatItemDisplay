package com.bingorufus.chatitemdisplay.displayables;

import com.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.api.display.DisplayType;
import com.bingorufus.chatitemdisplay.api.display.Displayable;
import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.bingorufus.chatitemdisplay.util.iteminfo.InventoryData;
import com.bingorufus.chatitemdisplay.util.iteminfo.InventorySerializer;
import com.bingorufus.chatitemdisplay.util.string.StringFormatter;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class DisplayEnderChest extends Displayable {
    protected Inventory inventory;
    protected String inventoryTitle;

    public DisplayEnderChest(Player player) {
        super(player);
        inventoryTitle = StringFormatter
                .format(getType().getInventoryTitle().replace("%player%",
                        ChatItemConfig.getConfig().getBoolean("use-nicks-in-gui") ? ChatItemConfig.getConfig().getBoolean("strip-nick-colors-gui")
                                ? ChatColor.stripColor(getDisplayer().getDisplayName())
                                : getDisplayer().getDisplayName() : getDisplayer().getRegularName()));
        inventory = Bukkit.createInventory(player, InventoryType.ENDER_CHEST, inventoryTitle);
        inventory.setContents(player.getEnderChest().getContents().clone());
    }

    public DisplayEnderChest(JsonObject data) {
        super(data);
    }

    @Override
    protected Class<? extends DisplayType<?>> getTypeClass() {
        return DisplayEnderChestType.class;
    }

    @Override
    public BaseComponent getDisplayComponent() {
        String format = StringFormatter
                .format(ChatItemConfig.CHAT_INVENTORY_FORMAT.getCachedValue())
                .replaceAll("%player%", ChatItemConfig.getConfig().getBoolean("use-nicks-in-display-message")
                        ? ChatItemConfig.getConfig().getBoolean("strip-nick-colors-message")
                        ? ChatColor.stripColor(getDisplayer().getDisplayName())
                        : getDisplayer().getDisplayName()
                        : getDisplayer().getRegularName());
        return format(format);
    }

    private TextComponent format(String format) {
        String[] parts = format.split("((?<=%type%)|(?=%type%))");
        TextComponent whole = new TextComponent();
        BaseComponent prev = null;
        for (int i = 0; i < parts.length; i++) {
            if (i > 0)
                prev = new TextComponent(TextComponent.fromLegacyText(whole.getExtra().get(i - 1).toLegacyText()));
            String part = parts[i];
            if (part.equalsIgnoreCase("%type%")) {
                TranslatableComponent type = new TranslatableComponent("container.enderchest");
                if (i > 0) {
                    type.copyFormatting(prev, ComponentBuilder.FormatRetention.FORMATTING, false);
                }
                whole.addExtra(type);
                continue;
            }

            TextComponent tc = new TextComponent(TextComponent.fromLegacyText(part));
            if (i > 0 && !part.startsWith("§r"))
                tc.copyFormatting(prev, ComponentBuilder.FormatRetention.FORMATTING, false);

            whole.addExtra(tc);
        }
        UUID id = ChatItemDisplayAPI.getDisplayedManager().getDisplay(this).getId();
        whole.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewitem " + id.toString()));

        return whole;
    }

    @Override
    public Inventory onViewDisplay(Player viewer) {
        return InventorySerializer.cloneInventory(this.inventory, this.inventoryTitle);
    }

    @Override
    public String getLoggerMessage() {

        String format = ChatItemConfig.CHAT_INVENTORY_FORMAT.getCachedValue()
                .replaceAll("%player%", ChatItemConfig.getConfig().getBoolean("use-nicks-in-display-message")
                        ? ChatItemConfig.getConfig().getBoolean("strip-nick-colors-message")
                        ? ChatColor.stripColor(getDisplayer().getDisplayName())
                        : getDisplayer().getDisplayName()
                        : getDisplayer().getRegularName());
        format = format.replaceAll("%type%", ChatItemDisplay.getInstance().getLang().get("container.enderchest").getAsString());

        return ChatColor.stripColor(StringFormatter.format(format));
    }

    @Override
    protected JsonObject serializeData() {
        JsonObject jo = new JsonObject();
        jo.addProperty("data", InventorySerializer.serialize(inventory, inventoryTitle));
        return jo;
    }

    @Override
    protected void deseralizeData(JsonObject data) {
        InventoryData inventoryData = InventorySerializer.deserialize(data.get("data").getAsString());
        inventory = inventoryData.getInventory();
        inventoryTitle = inventoryData.getTitle();
    }

    @Override
    public void broadcastDisplayable() {
        String format = StringFormatter
                .format(ChatItemConfig.COMMAND_INVENTORY_FORMAT.getCachedValue())
                .replaceAll("%player%",
                        ChatItemConfig.getConfig().getBoolean("use-nicks-in-display-message")
                                ? ChatItemConfig.getConfig().getBoolean("strip-nick-colors-message")
                                ? ChatColor.stripColor(getDisplayer().getDisplayName())
                                : getDisplayer().getDisplayName()
                                : getDisplayer().getRegularName());

        Bukkit.spigot().broadcast(format(format));
    }

    @Override
    public boolean hasBlacklistedItem() {
        return containsBlacklistedItem(inventory);
    }
}
