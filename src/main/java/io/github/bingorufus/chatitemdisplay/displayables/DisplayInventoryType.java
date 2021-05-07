package io.github.bingorufus.chatitemdisplay.displayables;

import com.google.gson.JsonObject;
import io.github.bingorufus.chatitemdisplay.api.display.DisplayType;
import io.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import org.bukkit.entity.Player;

import java.util.List;

public class DisplayInventoryType extends DisplayType<DisplayInventory> {

    @Override
    public List<String> getTriggers() {
        return ChatItemConfig.INVENTORY_TRIGGERS;
    }

    @Override
    public String getPermission() {
        return "ChatItemDisplay.display.inventory";
    }

    @Override
    public String getCommandPermission() {
        return "chatitemdisplay.command.display.inventory";
    }

    @Override
    public String getCommand() {
        return "displayinventory";
    }

    @Override
    public String getTooLargeMessage() {
        return ChatItemConfig.TOO_LARGE_INVENTORY;
    }

    @Override
    public String getMissingPermissionMessage() {
        return ChatItemConfig.MISSING_PERMISSION_INVENTORY;
    }

    @Override
    public DisplayInventory initDisplayable(Player player) {
        return new DisplayInventory(player);
    }

    @Override
    public DisplayInventory initDisplayable(JsonObject data) {
        return new DisplayInventory(data);
    }
}
