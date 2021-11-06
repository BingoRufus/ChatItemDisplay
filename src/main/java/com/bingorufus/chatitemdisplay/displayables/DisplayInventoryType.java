package com.bingorufus.chatitemdisplay.displayables;

import com.google.gson.JsonObject;
import lombok.NonNull;
import org.bukkit.entity.Player;

public class DisplayInventoryType extends ConfigurationSerializedDisplayType<DisplayInventory> {


    @Override
    public String getPermission() {
        return "ChatItemDisplay.display.inventory";
    }

    @Override
    public String getCommandPermission() {
        return "chatitemdisplay.command.display.inventory";
    }

    @Override
    public @NonNull String dataPath() {
        return "display-types.inventory";
    }

    @Override
    public String getCommandDescription() {
        return "Display inventories";
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
