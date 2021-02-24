package com.github.bingorufus.chatitemdisplay.displayables;

import com.github.bingorufus.chatitemdisplay.api.display.DisplayType;
import com.github.bingorufus.chatitemdisplay.api.display.Displayable;
import com.github.bingorufus.chatitemdisplay.util.ChatItemConfig;

import java.util.List;

public class DisplayInventoryType extends DisplayType {
    @Override
    public Class<? extends Displayable> getDisplayableClass() {
        return DisplayInventory.class;
    }

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
}
