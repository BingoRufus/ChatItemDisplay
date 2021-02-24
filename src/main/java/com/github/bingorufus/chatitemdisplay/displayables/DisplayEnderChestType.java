package com.github.bingorufus.chatitemdisplay.displayables;

import com.github.bingorufus.chatitemdisplay.api.display.DisplayType;
import com.github.bingorufus.chatitemdisplay.api.display.Displayable;
import com.github.bingorufus.chatitemdisplay.util.ChatItemConfig;

import java.util.List;

public class DisplayEnderChestType extends DisplayType {
    @Override
    public Class<? extends Displayable> getDisplayableClass() {
        return DisplayEnderChest.class;
    }

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
}
