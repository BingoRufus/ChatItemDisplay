package com.bingorufus.chatitemdisplay.api.display;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class DisplayType<T extends Displayable> {

    public abstract List<String> getTriggers();

    public abstract String getPermission();

    public abstract String getCommandPermission();

    public abstract String getCommand();

    public abstract String getInventoryTitle();

    public abstract boolean isCommandEnabled();

    /**
     * These commands will be registered automatically, so do not register them in your plugin.yml
     *
     * @return the aliases
     */
    public List<String> getAliases() {
        return null;
    }

    public String getCommandDescription() {
        return "This command displays something";
    }

    public abstract String getTooLargeMessage();

    public abstract String getMissingPermissionMessage();

    public abstract T initDisplayable(Player player);

    public abstract T initDisplayable(JsonObject data);

    public boolean canBeCreated(Player p) {
        return true;
    }


}
