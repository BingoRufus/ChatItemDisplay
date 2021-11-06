package com.bingorufus.chatitemdisplay.displayables;

import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.bingorufus.chatitemdisplay.util.string.StringFormatter;
import com.google.gson.JsonObject;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DisplayItemType extends ConfigurationSerializedDisplayType<DisplayItem> {


    @Override
    public String getPermission() {
        return "ChatItemDisplay.display.item";
    }

    @Override
    public String getCommandPermission() {
        return "chatitemdisplay.command.display.item";
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
    public @NonNull String dataPath() {
        return "display-types.item";
    }

    @Override
    public boolean canBeCreated(Player p) {
        if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
            p.sendMessage(StringFormatter.format(ChatItemConfig.EMPTY_HAND.getCachedValue()));
            return false;
        }
        return true;
    }

}
