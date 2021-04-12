package io.github.bingorufus.chatitemdisplay.api.display;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class DisplayType {
    public abstract Class<? extends Displayable> getDisplayableClass();

    public abstract List<String> getTriggers();

    public abstract String getPermission();

    public abstract String getCommandPermission();

    public abstract String getCommand();

    public abstract String getTooLargeMessage();

    public abstract String getMissingPermissionMessage();


    public Displayable initDisplayable(Player player) {
        try {
            return getDisplayableClass().getConstructor(Player.class).newInstance(player);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Displayable initDisplayable(JsonObject data) {
        try {
            return getDisplayableClass().getConstructor(JsonObject.class).newInstance(data);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean canBeCreated(Player p) {
        return true;
    }

}
