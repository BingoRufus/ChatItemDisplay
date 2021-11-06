package com.bingorufus.chatitemdisplay.displayables;

import com.google.gson.JsonObject;
import lombok.NonNull;
import org.bukkit.entity.Player;

public class DisplayEnderChestType extends ConfigurationSerializedDisplayType<DisplayEnderChest> {

    @Override
    public String getPermission() {
        return "ChatItemDisplay.display.enderchest";
    }

    @Override
    public String getCommandPermission() {
        return "chatitemdisplay.command.display.enderchest";
    }

    @Override
    public @NonNull String dataPath() {
        return "display-types.enderchest";
    }

    @Override
    public DisplayEnderChest initDisplayable(Player player) {
        return new DisplayEnderChest(player);
    }

    @Override
    public DisplayEnderChest initDisplayable(JsonObject data) {
        return new DisplayEnderChest(data);
    }


}
