package io.github.bingorufus.chatitemdisplay.displayables;

import io.github.bingorufus.chatitemdisplay.api.display.DisplayType;
import io.github.bingorufus.chatitemdisplay.api.display.Displayable;
import io.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import io.github.bingorufus.chatitemdisplay.util.string.StringFormatter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class DisplayItemType extends DisplayType {
    @Override
    public Class<? extends Displayable> getDisplayableClass() {
        return DisplayItem.class;
    }

    @Override
    public List<String> getTriggers() {
        return ChatItemConfig.ITEM_TRIGGERS;
    }

    @Override
    public String getPermission() {
        return "ChatItemDisplay.display.item";
    }

    @Override
    public String getCommandPermission() {
        return "chatitemdisplay.command.display.item";
    }

    @Override
    public String getCommand() {
        return "displayitem";
    }

    @Override
    public String getTooLargeMessage() {
        return ChatItemConfig.TOO_LARGE_ITEM;
    }

    @Override
    public String getMissingPermissionMessage() {
        return ChatItemConfig.MISSING_PERMISSION_ITEM;
    }

    @Override
    public boolean canBeCreated(Player p) {
        if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
            p.sendMessage(new StringFormatter().format(ChatItemConfig.EMPTY_HAND));
            return false;
        }
        return true;
    }

}
