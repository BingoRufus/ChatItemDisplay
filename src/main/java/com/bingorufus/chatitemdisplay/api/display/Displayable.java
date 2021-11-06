package com.bingorufus.chatitemdisplay.api.display;

import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.displayables.DisplayingPlayer;
import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Displayable {
    private final DisplayingPlayer displayer;

    public Displayable(Player displayer) {
        if (!displayer.isValid()) {
            throw new IllegalArgumentException("The displayer is not valid. If the player is not online, use data instead");
        }
        this.displayer = new DisplayingPlayer(displayer.getDisplayName(), displayer.getName(), displayer);
    }

    /**
     * It is required that all children have this constructor and super it.
     *
     * @param data The json data that will be deserialized
     */
    public Displayable(JsonObject data) {
        displayer = new DisplayingPlayer(data.getAsJsonObject("displayer"));
        deseralizeData(data.getAsJsonObject("data"));
    }

    protected static boolean isBlacklisted(ItemStack item) {
            return ChatItemConfig.BLACKLISTED_ITEMS.getCachedValue().contains(item.getType());
    }

    protected static boolean containsBlacklistedItem(Inventory inventory) {
        for (ItemStack item : inventory.getStorageContents()) {
            if (item == null) continue;
            if (isBlacklisted(item)) return true;

            ItemMeta meta = item.getItemMeta();
            if (meta instanceof BlockStateMeta) {

                BlockStateMeta bsm = (BlockStateMeta) meta;
                if (bsm.getBlockState() instanceof Container) {

                    Container c = (Container) bsm.getBlockState();

                    if (containsBlacklistedItem(c.getInventory())) return true;
                }
            }

        }
        return false;
    }

    protected abstract Class<? extends DisplayType<?>> getTypeClass();

    public DisplayType<?> getType() {
        DisplayType<?> displayType = ChatItemDisplayAPI.getRegisteredDisplayables().stream().filter(type -> type.getClass().equals(getTypeClass())).findFirst().orElse(null);
        if (displayType == null) {
            Bukkit.getLogger().warning("Cannot find a displaytype that has the class path of: " + getTypeClass().getCanonicalName());
            return null;
        }
        return displayType;
    }

    public abstract BaseComponent getDisplayComponent();

    public abstract Inventory onViewDisplay(Player viewer);

    public abstract String getLoggerMessage();

    protected abstract JsonObject serializeData();

    protected abstract void deseralizeData(JsonObject data);

    public abstract void broadcastDisplayable();

    public DisplayingPlayer getDisplayer() {
        return this.displayer;
    }

    public JsonObject serialize() {
        JsonObject jo = new JsonObject();
        jo.addProperty("type", getType().getClass().getCanonicalName());
        jo.add("data", serializeData());
        jo.add("displayer", getDisplayer().serailze());
        return jo;
    }

    public abstract boolean hasBlacklistedItem();
}
