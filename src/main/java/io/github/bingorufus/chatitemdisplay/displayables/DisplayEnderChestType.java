package io.github.bingorufus.chatitemdisplay.displayables;

import com.google.gson.JsonObject;
import io.github.bingorufus.chatitemdisplay.api.display.DisplayType;
import io.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import org.bukkit.entity.Player;

import java.util.List;

public class DisplayEnderChestType extends DisplayType<DisplayEnderChest> {

    @Override
    public List<String> getTriggers() {
        return ChatItemConfig.ENDERCHEST_TRIGGERS;
    }

    @Override
    public String getPermission() {
        return "ChatItemDisplay.display.enderchest";
    }

    @Override
    public String getCommandPermission() {
        return "chatitemdisplay.command.display.enderchest";
    }

    @Override
    public String getCommand() {
        return "displayenderchest";
    }

    @Override
    public String getTooLargeMessage() {
        return ChatItemConfig.TOO_LARGE_ENDERCHEST;
    }

    @Override
    public String getMissingPermissionMessage() {
        return ChatItemConfig.MISSING_PERMISSION_ENDERCHEST;
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
