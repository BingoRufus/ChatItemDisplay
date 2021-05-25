package io.github.bingorufus.chatitemdisplay.displayables;

import com.google.gson.JsonObject;
import io.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import io.github.bingorufus.chatitemdisplay.api.display.DisplayType;
import io.github.bingorufus.chatitemdisplay.api.display.Displayable;
import io.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import io.github.bingorufus.chatitemdisplay.util.iteminfo.InventoryData;
import io.github.bingorufus.chatitemdisplay.util.iteminfo.InventorySerializer;
import io.github.bingorufus.chatitemdisplay.util.iteminfo.PlayerInventoryReplicator;
import io.github.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class DisplayInventory extends Displayable {
    protected Inventory inventory;
    protected String inventoryTitle;

    public DisplayInventory(JsonObject data) {
        super(data);
    }

    public DisplayInventory(Player displayer) {
        super(displayer);
        PlayerInventoryReplicator playerInventoryReplicator = new PlayerInventoryReplicator();
        InventoryData data = playerInventoryReplicator.replicateInventory(displayer);
        inventory = data.getInventory();
        inventoryTitle = data.getTitle();
    }

    @Override
    protected Class<? extends DisplayType<?>> getTypeClass() {
        return DisplayInventoryType.class;
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
                TranslatableComponent type = new TranslatableComponent("container.inventory");
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
        UUID id = ChatItemDisplay.getInstance().getDisplayedManager().getDisplay(this).getId();
        whole.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewitem " + (id)));

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
        format = format.replaceAll("%type%", ChatItemDisplay.getInstance().getLang().get("container.inventory").getAsString());

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
