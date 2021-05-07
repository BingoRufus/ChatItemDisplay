package io.github.bingorufus.chatitemdisplay.displayables;

import com.google.gson.JsonObject;
import io.github.bingorufus.chatitemdisplay.api.display.DisplayType;
import io.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import io.github.bingorufus.chatitemdisplay.util.string.StringFormatter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class DisplayItemType extends DisplayType<DisplayItem> {

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
    public String[] getAliases() {
        return new String[]{"display1", "display2"};
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
    public DisplayItem initDisplayable(Player player) {
        return new DisplayItem(player);
    }

    @Override
    public DisplayItem initDisplayable(JsonObject data) {
        return new DisplayItem(data);
    }

    @Override
    public boolean canBeCreated(Player p) {
        if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
            p.sendMessage(StringFormatter.format(ChatItemConfig.EMPTY_HAND));
            return false;
        }
        return true;
    }

}
